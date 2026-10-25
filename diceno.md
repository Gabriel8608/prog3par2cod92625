(a) DISEÑO DE CLASES
================================================================================

--------------------------------------------------------------------------------
A.1 DTOs DE LAS APIs (capa data/remote)
--------------------------------------------------------------------------------

Cada API tiene su propio DTO porque devuelve una estructura JSON distinta.

--- DTO 1: DummyJSON (Productos) -----------------------------------------------

La respuesta es un objeto con una lista "products" y metadatos total/skip/limit.
Se necesitan dos DTOs.

data class ProductsResponseDto(
    @SerializedName("products") val products: List<ProductDto>,
    @SerializedName("total")    val total: Int,
    @SerializedName("skip")     val skip: Int,
    @SerializedName("limit")    val limit: Int
)

data class ProductDto(
    @SerializedName("id")                 val id: Int,
    @SerializedName("title")              val title: String,
    @SerializedName("description")        val description: String,
    @SerializedName("category")           val category: String,
    @SerializedName("price")              val price: Double,
    @SerializedName("discountPercentage") val discountPercentage: Double,
    @SerializedName("rating")             val rating: Double,
    @SerializedName("stock")              val stock: Int,
    @SerializedName("brand")              val brand: String?,
    @SerializedName("thumbnail")          val thumbnail: String,
    @SerializedName("images")             val images: List<String>
)

Atributos relevantes:
  - id, title, description, category
  - price: precio en USD
  - discountPercentage, rating, stock, brand: datos extra que se pueden mostrar
  - thumbnail: URL de la imagen principal
  - images: lista de URLs de imágenes

--- DTO 2: Frankfurter (Tipos de cambio) ----------------------------------------

/v2/rates devuelve una LISTA de filas, no un objeto. Cada fila tiene una moneda
cotizada. Por eso el DTO es una sola clase y la respuesta se maneja como
List<RateDto>.

data class RateDto(
    @SerializedName("date")  val date: String,   // "YYYY-MM-DD"
    @SerializedName("base")  val base: String,   // "USD"
    @SerializedName("quote") val quote: String,  // "EUR" o "BOB"
    @SerializedName("rate")  val rate: Double    // unidades de quote por 1 base
)

Respuesta esperada (lista de 2 filas):
  [
    { "date": "...", "base": "USD", "quote": "EUR", "rate": 0.87 },
    { "date": "...", "base": "USD", "quote": "BOB", "rate": 6.9  }
  ]

Atributos relevantes:
  - date:  fecha de la cotización
  - base:  moneda base (USD)
  - quote: moneda destino (EUR o BOB)
  - rate:  tasa de conversión (monto * rate = resultado)

--- DTO 3: JSONPlaceholder (Publicaciones) --------------------------------------

La respuesta es una lista directa de publicaciones.

data class PostDto(
    @SerializedName("userId") val userId: Int,
    @SerializedName("id")     val id: Int,
    @SerializedName("title")  val title: String,
    @SerializedName("body")   val body: String
)

Atributos relevantes:
  - userId: autor
  - id:     identificador
  - title:  título
  - body:   contenido (puede tener saltos de línea)

--------------------------------------------------------------------------------
A.2 MODELO DEL DOMINIO (capa domain)
--------------------------------------------------------------------------------

El modelo de dominio es lo que consume la UI. Es independiente de las APIs y de
Room. Se usan modelos separados por pantalla.

--- Modelo de producto (Catálogo y Detalle) ------------------------------------

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val priceUsd: Double,
    val rating: Double,
    val stock: Int,
    val brand: String?,
    val thumbnail: String
)

--- Modelo de tipo de cambio (Detalle) -----------------------------------------

Se construye a partir de List<RateDto> buscando las filas "EUR" y "BOB".

data class ExchangeRates(
    val base: String,   // "USD"
    val eur: Double,    // tasa USD -> EUR
    val bob: Double,    // tasa USD -> BOB
    val date: String    // fecha de la cotización
)

--- Modelo de publicación (Pantalla de Publicaciones) --------------------------

data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)

--- Modelo de favorito (Favoritos) ---------------------------------------------

data class FavoriteProduct(
    val id: Int,
    val title: String,
    val priceUsd: Double,
    val thumbnail: String,
    val note: String
)

Relaciones entre modelos:
  - Un Product se puede convertir en un FavoriteProduct (al marcarlo favorito).
  - ExchangeRates se aplica sobre Product.priceUsd para mostrar EUR y BOB.
    No se persiste (es un dato volátil).
  - Post es totalmente independiente de Product (no hay relación).

--------------------------------------------------------------------------------
A.3 ENTIDAD DE ROOM (capa data/local)
--------------------------------------------------------------------------------

Solo se persisten los productos favoritos. Una única entidad.

@Entity(tableName = "favorite_products")
data class FavoriteProductEntity(
    @PrimaryKey
    val id: Int,                 // mismo id del producto de DummyJSON
    val title: String,
    val priceUsd: Double,
    val thumbnail: String,
    val note: String = ""        // nota personal del usuario
)

Relaciones:
  - No hay relaciones foráneas. Tabla independiente y autocontenida.
  - El "id" coincide con el id del Product de DummyJSON, lo que permite saber
    si un producto ya está guardado (evita duplicados).


================================================================================
(b) DISEÑO DE LA BASE DE DATOS
================================================================================

Motor: Room (SQLite)
Nombre de la BD: app_database
Versión: 1

--------------------------------------------------------------------------------
Tabla: favorite_products
--------------------------------------------------------------------------------

+----------------+---------+------------------------------------------+---------+
| Columna        | Tipo    | Descripción                              | Clave   |
+----------------+---------+------------------------------------------+---------+
| id             | INTEGER | Id del producto (viene de DummyJSON)     | PK      |
| title          | TEXT    | Nombre del producto                      |         |
| priceUsd       | REAL    | Precio en USD al momento de guardar      |         |
| thumbnail      | TEXT    | URL de la imagen principal               |         |
| note           | TEXT    | Nota personal del usuario (puede vacía)  |         |
+----------------+---------+------------------------------------------+---------+

Clave primaria: id (INTEGER, NO autogenerada; se toma del producto original).

Decisiones de diseño:
  - id no es autogenerado: se reutiliza el id del producto de DummyJSON para
    evitar duplicados (un producto solo puede estar una vez en favoritos).
  - priceUsd se guarda como REAL para conservar el precio histórico.
  - note se guarda como TEXT con valor por defecto "".
  - No se guarda la tasa de cambio: es volátil y se consulta en vivo.

Operaciones previstas sobre la tabla (DAO):
  - Obtener todos los favoritos como Flow<List<...>>  (observable en la UI)
  - Insertar o reemplazar un favorito (OnConflictStrategy.REPLACE)
  - Eliminar un favorito por id (@Query DELETE)
  - Actualizar la nota de un favorito
  - Verificar si un producto ya es favorito (por id)

Estados de la pantalla de Favoritos:
  - Lista con elementos -> se muestra el listado.
  - Lista vacía          -> se muestra "Sin elementos guardados".


================================================================================
(c) CAPAS DE MVVM Y SU CONTENIDO
================================================================================

El proyecto sigue el patrón MVVM con separación en capas: data (remota + local +
repositorio), domain (modelos) y ui (pantallas + ViewModels).

--------------------------------------------------------------------------------
C.1 CAPA DATA (data/)
--------------------------------------------------------------------------------

data/remote/dummyjson/
  - ProductDto, ProductsResponseDto
  - DummyJsonApi   -> @GET("products") suspend fun products(limit, skip): ProductsResponseDto

data/remote/frankfurter/
  - RateDto
  - FrankfurterApi -> @GET("v2/rates") suspend fun rates(base, quotes): List<RateDto>

data/remote/jsonplaceholder/
  - PostDto
  - JsonPlaceholderApi -> @GET("posts") suspend fun posts(_start, _limit): List<PostDto>

data/remote/
  - RetrofitInstances -> una instancia de Retrofit por baseUrl:
        * DummyJSON:       https://dummyjson.com/
        * Frankfurter:     https://api.frankfurter.dev/
        * JSONPlaceholder: https://jsonplaceholder.typicode.com/

data/local/
  - FavoriteProductEntity  -> entidad de Room
  - FavoriteProductDao     -> DAO con Flow y suspend
  - AppDatabase            -> base de datos con instancia única (singleton)

data/repository/
  - CatalogRepository   -> llama a DummyJsonApi, mapea ProductDto -> Product.
                           Devuelve Result<List<Product>>.
  - CurrencyRepository  -> llama a FrankfurterApi, recibe List<RateDto>, busca
                           las filas EUR y BOB y construye ExchangeRates.
                           Devuelve Result<ExchangeRates>.
  - PostRepository      -> llama a JsonPlaceholderApi, mapea PostDto -> Post.
                           Devuelve Result<List<Post>>.
  - FavoriteRepository  -> operaciones CRUD sobre Room (favoritos).

Todos los repositorios devuelven Result<T> y manejan errores (red, HTTP no
exitosa, excepciones). Aquí se hace el mapeo DTO -> modelo de dominio.

--------------------------------------------------------------------------------
C.2 CAPA DOMAIN (domain/)
--------------------------------------------------------------------------------

  - Product
  - ExchangeRates
  - Post
  - FavoriteProduct

Modelos puros, sin dependencias de Retrofit ni Room.

--------------------------------------------------------------------------------
C.3 CAPA UI (ui/)
--------------------------------------------------------------------------------

ui/screens/
  - CatalogScreen        -> lista de productos + buscador + acceso a Publicaciones
  - ProductDetailScreen  -> detalle + conversión de moneda + "Marcar favorito"
  - PostsScreen          -> lista de publicaciones (pantalla independiente)
  - FavoritesScreen      -> lista de favoritos + quitar + mensaje de lista vacía

ui/viewmodel/
  - CatalogViewModel
      * UiState: Loading / Success(products) / Error(message)
      * StateFlow<CatalogUiState>
      * query como StateFlow + derivedStateOf para filtrar
      * acción: reintentar()
  - ProductDetailViewModel
      * UiState del producto
      * UiState de las tasas (Loading / Success(ExchangeRates) / Error)
      * acciones: cargarTasas(), marcarFavorito()
  - PostsViewModel
      * UiState: Loading / Success(posts) / Error(message)
      * acción: reintentar()
  - FavoritesViewModel
      * observa el Flow de favoritos de Room
      * acción: quitarFavorito(id)
      * expone si la lista está vacía para el mensaje "Sin elementos guardados"

ui/theme/ y MainActivity -> punto de entrada y tema.

--------------------------------------------------------------------------------
C.4 FLUJO DE DATOS (resumen)
--------------------------------------------------------------------------------

  Pantalla (Compose)
        |
        v
  ViewModel (UiState + StateFlow)
        |
        v
  Repository (Result<T>, mapeo DTO -> dominio)
        |
        +--> API remota (Retrofit + DTO)
        +--> Room (Entity + DAO)

  - La UI solo observa StateFlow y llama funciones del ViewModel.
  - El ViewModel nunca llama a Retrofit ni a Room directamente: siempre pasa
    por el repositorio.
  - El repositorio es el único que conoce DTOs, entidades y APIs.

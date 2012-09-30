package org.milmsearch.core.api

import java.io.File
import java.net.URI
import javax.ws.rs.core.Response.Status
import javax.ws.rs.core.Response
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.HeaderParam
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import net.liftweb.json.DefaultFormats
import net.liftweb.json.FieldSerializer
import net.liftweb.json.Serialization
import net.liftweb.json.ShortTypeHints
import net.liftweb.json.parse
import javax.ws.rs.Consumes

@Path("/samplelist")
class SampleListResource {
  
  /**
   * samplelist リストリソースの取得
   */
  @GET
  def getList(): Response = {
    // 予め item1 と item2 が登録されていて、それを取得したとする
    
    val res =
      <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
          <title>get sample list</title>
        </head>
        <body>
          <ul>
            <li>item1</li>
            <li>item2</li>
          </ul>
        </body>
      </html>
    // ok メソッドは ステータス「200 OK」のこと
    Response.ok(res.toString()).build()
  }
  
  /**
   * アイテムリソースを取得します。
   * 
   * @param itemName 更新するアイテムの名前
   */
  @GET
  @Path("/{itemName}")
  def getItem(@PathParam("itemName")itemName: String): Response = {
    // itemName のアイテムを取得する処理をしたとする
    
    val res =
      <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
          <title>get item {itemName}</title>
        </head>
        <body>
          <dl>
            <dt>{itemName}</dt>
            <dd>{itemName} の中身 サンプルなのでハードコーディング</dd>
          </dl>
        </body>
      </html>
            
    // ok メソッドは ステータス「200 OK」のこと
    Response.ok(res.toString()).build()
  }
  
  /**
   * リソースの作成
   * POSTなので、itemName(URLの一部)を自動生成するパターン
   * PUTで作成する場合は itemName をURLに含めて、それが存在しないURLの場合は
   * 作成するようにするのが一般的。
   * 
   * @param content item の中身。HTTPのボディの文字列。
   */
  @POST
  def addList(content: String): Response = {
    // itemName が item3、中身が content のアイテムを追加するとする
    
    // ステータスコード 201 created
    // Location ヘッダ "/samplelist/item3" (本当はフルパスが良いかも)
    Response.created(new URI("/samplelist/item3")).build()
  }
 
  /**
   * リソースの更新
   * 
   * @param itemName 更新するアイテムの名前
   * @param content  item の中身。HTTPボディの文字列。 
   */
  @PUT
  @Path("/{itemName}")
  def editList(@PathParam("itemName")itemName: String, content: String): Response = {
    // itemName のアイテムの内容を content で更新する処理をしたとする
    
    val res = content
    Response.ok(res.toString()).build()
  }
  
  /**
   * リソースの削除
   * 
   * @param itemName 更新するアイテムの名前
   */
  @DELETE
  @Path("/{itemName}")
  def deleteList(@PathParam("itemName")itemName: String): Response = {
    // itemName のアイテムを削除する処理をしたとする
    
    Response.ok().build()
  }
  
  /**
   * クエリパラメータのサンプル
   */
  @GET
  @Path("/search")
  def search(@QueryParam("q") query: String): Response = {
    if (query == null) {
      Response.status(Status.BAD_REQUEST).build()
    }
    
    // 検索処理をしたとする
    
    Response.ok(query + "で検索した結果").build()
  }
  
  /**
   * ヘッダ取得サンプル
   */
  @GET
  @Path("/header")
  def getHeader(@HeaderParam("Accept") accept: String): Response = {
    Response.ok("Acceptヘッダ: " + accept).build()
  }  

  /**
   * フォームからPOSTするサンプル
   * テストアクセス用のHTML
   * 引数の変数名とnameを合わせる。QueryPramは必要なし。
   * 
  <html>
  <form method="post" action="localhost:8080/api/samplelist/search">
    <input type="text" name="q" />
    <input type="submit" value="送信">
  </form>
  </html>
   * 
   */
  @POST
  @Path("/search")
  def searchPost(q: String): Response = {
    if (q == null) {
      Response.status(Status.BAD_REQUEST).build()
    }
    
    // 検索処理をしたとする
    
    Response.ok(q + "で検索した結果(POST)").build()
  }
  
  // jsonのパースのフォーマット指定。暗黙的パラメータに使用される。
  implicit val formats = DefaultFormats

  // ケースクラスをJSONのフォーマットに合わせた入れ子にして、JSON からパースできる
  case class Address(zipcode: String, country: String)
  case class Person(name: String, address: Address)

  /**
   * JSONリクエストサンプル
   * bodyStr にJSON文字列が入る。
   * 
   * 送信するJSONの例
{
 "name": "Mizuki",
 "address": {
    "zipcode": "123-4555",
    "country": "Japan"
  }
}
   */
  @POST
  @Path("/person")
  def addPerson(bodyStr: String): Response = {

    // extract のジェネリクスで何の型にパースするかを指定
    val person = parse(bodyStr).extract[Person]
    
    Response.ok(person.toString()).build()
  }

  /**
   * JSONレスポンスサンプル
   */
  @GET
  @Path("/person")
  def getPerson(): Response = {
    val person = Person("Milm", Address("333-3333", "Japan"))
    val json = Serialization.write(person)  //TODO 自動的に $outer てのが入っちゃう。出さない方法がわからぬ     
    Response.ok(json).build()
  }
  
  /**
   * CSVファイルを取得する
   */
  @GET
  @Path("/sample.csv")
  def getCSVFile(): Response = {
    Response.ok(new File("sample.csv"), "text/csv").build()
  }
  
  /**
   * Acceptヘッダで指定されたので XML を取得する
   */
  @GET
  @Path("/data")
  @Produces(Array("text/xml"))
  def getXmlByProduces(): Response = {
    val xml = 
      <child>
        <name>たろう</name>
      </child>
    Response.ok(xml.toString(),"text/xml").build()    
  }

  /**
   * Acceptヘッダで指定されたので JSON を取得する
   */
  @GET
  @Path("/data")
  @Produces(Array("application/json"))
  def getJsonByProduces(): Response = {
    val person = Person("Milm", Address("333-3333", "Japan"))
    val json = Serialization.write(person)     
    Response.ok(json).build()    
  }
  
/**
 * おまけ
 * XML と JSON の相互変換の仕方
 * 
 * 【Json -> XML】
 * Xml.toXml(json)
 * 
 * 【XML -> Json】
 * Serialization.write(Xml.toJson(xml))
 */
}
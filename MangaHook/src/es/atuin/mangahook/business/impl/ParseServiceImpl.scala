package es.atuin.mangahook.business.impl

import scala.util.matching.Regex

import org.apache.log4j.Logger

import es.atuin.mangahook.business.ParseService
import es.atuin.mangahook.constants.MangaHookConstants
import es.atuin.mangahook.model.Source
import es.atuin.mangahook.thread.DownloadThread
import scalaj.http.Http
import scalaj.http.HttpOptions
import scalaj.http.HttpRequest
import scalaj.http.HttpResponse

/**
 * <p>Implementación del servicio de parseo.<br>
 * Es un <i>object</i> para utilizar el patrón <i>Singleton</i>.</p>
 * 
 * @author damores
 */
object ParseServiceImpl extends ParseService {
  
  private val logger: Logger = Logger.getLogger(ParseServiceImpl.getClass);
  
  def obtainAll(source: Source, localFolder: String): Unit = {
    parseRequest(source, localFolder, 1)
  }
  
  def obtainFromChapter(source: Source, localFolder: String, startingChapter: Int): Unit = {
    parseRequest(source, localFolder, startingChapter)
  }
  
  /**
   * <p>Método que resuelve la expresión regular a utilizar para 
   * parsear el contenido en función del origen de datos.</p>
   * 
   */
  private def resolveRegex(source: Source): Regex = source.url match {
    case MangaHookConstants.mangapandaPattern(source) => "<img[\\s\\w0-9\"=-]*(id=\"img\")[\\s\\w0-9\"=-]*src=\"((http|https)://([a-zA-Z0-9\\.-]+){1}(/[a-zA-Z0-9\\.-]+)*)\"[\\s\\w0-9\"=-]*/>".r
    case MangaHookConstants.submangaPattern(source) => throw new UnsupportedOperationException(" ## ERROR: Operación en construcción ")
    case _ => throw new UnsupportedOperationException(" ## ERROR: Operación sin implementar, contacte con el administrador ")
  }
  
  /**
   * <p>Método que realiza el parseo de la fuente, capítulo a capítulo y 
   * página a página.</p>
   * 
   * @param source <code>Source</code> con los datos de la fuente a parsear.
   * @param localFolder <code>String</code> con la ruta del directorio local 
   * sobre el que se almacenaran los contenidos.
   * @param startingChapter <code>Int</code> con el número de capítulo desde 
   * el que se debe empezar a parsear.
   */
  private def parseRequest(source: Source, localFolder: String, startingChapter: Int): Unit = {
    
    val regex: Regex = this.resolveRegex(source)
    var chapterCounter: Int = startingChapter
    var pageCounter: Int = 1
    var continuar: Boolean = true
    var continuarCapitulo: Boolean = true
    
    // 1. Continuamos mientras queden capítulos
    while (continuar)
    {
      pageCounter = 1
      continuarCapitulo = true
      logger.info(" >> Procesando capitulo " + chapterCounter)
      
      val requestCapitulo: HttpRequest =
          Http(source.url + "/" + chapterCounter + "/" + pageCounter)
            .option(HttpOptions.connTimeout(60000))
            
      val responseCapitulo: HttpResponse[String] = requestCapitulo.asString
      if (responseCapitulo.body.contains("<div id=\"recom\">"))
      {
        continuarCapitulo = false;
        continuar = false;
      }
      
      // 2. Continuamos mientras queden páginas
      while (continuarCapitulo)
      {
        logger.info(" >> Procesando página " + pageCounter)
        val requestPagina: HttpRequest =
          Http(source.url + "/" + chapterCounter + "/" + pageCounter)
            .option(HttpOptions.connTimeout(60000))
    
        val response: HttpResponse[String] = requestPagina.asString
        
        if (!response.isError)
        {
          // 3. Configuramos e invocamos el hilo que se encargará de 
          //      realizar la descarga en paralelo al procesamiento de URLs.
          regex.findAllIn(response.body.intern()).matchData foreach {
            g => {
              var download: DownloadThread = new DownloadThread;
              download.setUrlStr(g.group(2))
              download.setLocalFolder(localFolder)
              download.setChapterCounter(chapterCounter)
              download.setPageCounter(pageCounter)
              download.run()
              }
          }
          pageCounter += 1
        }
        else
        {
          logger.error(" # ERROR: " + response.body)
          continuarCapitulo = false;
        }
      }
    
      logger.info(" >> Fin de capitulo " + chapterCounter)
      chapterCounter += 1
    }
  }
}
package es.atuin.mangahook.thread

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

import org.apache.log4j.Logger

/**
 * <p>Método <i>Runnable</i> creado para permitir realizar la 
 * descarga de contenido en hilos paralelos al principal a fin 
 * de agilizar la descarga.</p>
 * 
 * @author damores
 */
class DownloadThread extends Runnable {
  
  private val logger: Logger = Logger.getLogger(this.getClass)
  
  private var localFolder: String = null;
  private var urlStr: String = null;
  private var chapterCounter: Integer = null;
  private var pageCounter: Integer = null;
  
  /**
   * <p>Método que recupera la imagen de la fuente y la descarga al 
   * directorio local sobre la ruta indicada con un formato determinado 
   * para garantizar el orden.</p>
   * 
   * @param urlStr <code>String</code> con la url en la que se encuentra 
   * la imagen a descargar.
   * @param localFolder <code>String</code> con la ruta del directorio donde 
   * se guardará la imagen.
   * @param chapterCounter <code>Int</code> con el número de capítulo al que 
   * pertenece la imagen.
   * @param pageCounter <code>Int</code> con el número de página de la imagen.
   */
  private def parseImage(urlStr: String, localFolder: String, chapterCounter: Int, pageCounter: Int): Unit = {
    
    var in: InputStream = null;
    var out: OutputStream = null;
    
    val folderName: String = "%03d".format(chapterCounter)
    val fileName: String = "%03d".format(pageCounter)
    
    try
    {
      val url = new URL(urlStr)
      val connection: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
      connection.setRequestMethod("GET")
      connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
      
      val chapterFolder: File = new File(localFolder + File.separator + folderName)
      if (!chapterFolder.exists())
      {
        chapterFolder.mkdirs();
      }
      
      in = connection.getInputStream
      out = new BufferedOutputStream(new FileOutputStream(chapterFolder.getAbsolutePath + File.separator + fileName + ".jpg"))
      
      val byteArray = Stream.continually(in.read).takeWhile(-1 != _).map(_.toByte).toArray

      out.write(byteArray)
    }
    catch
    {
      case io: IOException => logger.error(" # IO-ERROR: Couldn't parse page " + pageCounter + " from chapter " + chapterCounter, io)
      case e: Exception => logger.error(" # ERROR: Couldn't parse page " + pageCounter + " from chapter " + chapterCounter, e)
    }
    finally
    {
      if (out != null)
      {
        out.close()
      }
      
      if (in != null)
      {
        in.close()
      }
    }
  }
  
  def run {
    parseImage(urlStr, localFolder, chapterCounter, pageCounter)
  }
  
  def setLocalFolder(localFolder: String): Unit = 
    this.localFolder = localFolder
  
  def setUrlStr(urlStr: String): Unit = 
    this.urlStr = urlStr;
  
  def setChapterCounter(chapterCounter: Int): Unit = 
    this.chapterCounter = chapterCounter;
  
  def setPageCounter(pageCounter: Int): Unit = 
    this.pageCounter = pageCounter;
}
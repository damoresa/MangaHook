package es.atuin.mangahook.launcher

import es.atuin.mangahook.business.ParseService
import es.atuin.mangahook.business.impl.ParseServiceImpl
import es.atuin.mangahook.model.Source

/**
 * <p>Método principal del programa.</p>
 * <p>Es un <i>object</i> para hacer uso del patrón 
 * <i>Singleton</i>.</p>
 * 
 * @author damores
 */
object Launcher {
  
  /**
   * Interfaz del servicio de parseo en la que se inyecta la implementación 
   * Singleton del mismo.
   */
  private[this] val parseService: ParseService = ParseServiceImpl

  def main(args: Array[String]): Unit = {
    
    if (args.length < 3)
    {
      throw new IllegalArgumentException(" >> Faltan parámetros para la ejecución del programa ");
    }
    
    val source: Source = new Source(args(0))
    val startingChapter: Int = args(1).toInt
    val localFolder: String = args(2)
    
    if (startingChapter > 0)
    {
      parseService.obtainFromChapter(source, localFolder, startingChapter)
    }
    else
    {
      parseService.obtainAll(source, localFolder)
    }
  }
}
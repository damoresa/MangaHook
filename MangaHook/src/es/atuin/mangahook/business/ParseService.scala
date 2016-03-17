package es.atuin.mangahook.business

import es.atuin.mangahook.model.Source

/**
 * <p><code>Trait</code> a modo de interfaz para el servicio de 
 * parseo de fuentes.</p>
 * 
 * @author damores
 */
trait ParseService {
  
  /**
   * <p>Método que, indicada una fuente y un directorio local destino, 
   * parsea y recupera el contenido obtenido desde la fuente.</p>
   * 
   * @param source <code>Source</code> con los datos de la fuente de datos.
   * @param localFolder <code>String</code> con los datos del directorio 
   * local sobre el que se almacenarán los datos parseados.
   */
  def obtainAll(source: Source, localFolder: String): Unit
  
  /**
   * <p>Método que, indicada una fuente, un capítulo inicial y un directorio 
   * local destino, parsea y recupera el contenido obtenido desde la fuente.</p>
   * 
   * @param source <code>Source</code> con los datos de la fuente de datos.
   * @param localFolder <code>String</code> con los datos del directorio 
   * local sobre el que se almacenarán los datos parseados.
   * @param startingChapter <code>Int</code> con el número de capítulo desde el 
   * que se debe empezar
   */
  def obtainFromChapter(source: Source, localFolder: String, startingChapter: Int): Unit
}
package es.atuin.mangahook.constants

import scala.util.matching.Regex

/**
 * <p>Constantes de la aplicación.<br>
 * Actualmente sólo se almacenan las expresiones regulares que 
 * detectan las diferentes fuentes para poder obtener la <i>regex</i> 
 * correcta para cada caso.</p>
 * 
 * @author damores
 */
object MangaHookConstants {
  
  val mangapandaPattern: Regex = ".*(mangapanda\\.com).*"r
  val submangaPattern: Regex = ".*(submanga\\.com).*"r
  
}
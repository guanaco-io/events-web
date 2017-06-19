
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/gertv/Projects/anova/guanaco-web/conf/routes
// @DATE:Tue May 23 20:56:39 CEST 2017


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}


package views.html

import play.twirl.api._
import play.twirl.api.TemplateMagic._


     object main_Scope0 {
import models._;import controllers._;import play.api.templates.PlayMagic._;import play.api.i18n._;import play.api.mvc._;import play.api.data._;import views.html._;

class main extends BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with play.twirl.api.Template2[String,Html,play.twirl.api.HtmlFormat.Appendable] {

  /*
 * This template is called from the `index` template. This template
 * handles the rendering of the page header and body tags. It takes
 * two arguments, a `String` for the title of the page and an `Html`
 * object to insert into the body of the page.
 */
  def apply/*7.2*/(title: String)(content: Html):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*7.32*/("""

"""),format.raw/*9.1*/("""<!DOCTYPE html>
<html lang="en">
    <head>
        """),format.raw/*12.62*/("""
        """),format.raw/*13.9*/("""<title>"""),_display_(/*13.17*/title),format.raw/*13.22*/("""</title>
        <link rel="stylesheet" media="screen" href=""""),_display_(/*14.54*/routes/*14.60*/.Assets.at("stylesheets/main.css")),format.raw/*14.94*/("""">
        <link rel="shortcut icon" type="image/png" href=""""),_display_(/*15.59*/routes/*15.65*/.Assets.at("images/favicon.png")),format.raw/*15.97*/("""">
        <script src=""""),_display_(/*16.23*/routes/*16.29*/.Assets.at("elmMain.js")),format.raw/*16.53*/("""" type="text/javascript"></script>
    </head>
    <body>
        """),format.raw/*20.32*/("""
        """),format.raw/*21.9*/("""<nav class="navbar navbar-inverse navbar-fixed-top">
            <div class="container">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="#">Project name</a>
                </div>
                <div id="navbar" class="collapse navbar-collapse">
                    <ul class="nav navbar-nav">
                        <li class="active"><a href="#">Home</a></li>
                        <li><a href="#about">About</a></li>
                        <li><a href="#contact">Contact</a></li>
                    </ul>
                </div><!--/.nav-collapse -->
            </div>
        </nav>

        <div class="container">

            <div class="starter-template">
                <h1>Bootstrap starter template</h1>
                <p class="lead">Use this document as a way to quickly start any new project.<br> All you get is this text and a mostly barebones HTML document.</p>
                """),_display_(/*47.18*/content),format.raw/*47.25*/("""
                """),format.raw/*48.17*/("""<div id="test">Empty div</div>
            </div>

        </div><!-- /.container -->

      <script src=""""),_display_(/*53.21*/routes/*53.27*/.Assets.at("javascripts/main.js")),format.raw/*53.60*/("""" type="text/javascript"></script>
    </body>
</html>
"""))
      }
    }
  }

  def render(title:String,content:Html): play.twirl.api.HtmlFormat.Appendable = apply(title)(content)

  def f:((String) => (Html) => play.twirl.api.HtmlFormat.Appendable) = (title) => (content) => apply(title)(content)

  def ref: this.type = this

}


}

/*
 * This template is called from the `index` template. This template
 * handles the rendering of the page header and body tags. It takes
 * two arguments, a `String` for the title of the page and an `Html`
 * object to insert into the body of the page.
 */
object main extends main_Scope0.main
              /*
                  -- GENERATED --
                  DATE: Tue May 23 20:56:41 CEST 2017
                  SOURCE: /home/gertv/Projects/anova/guanaco-web/app/views/main.scala.html
                  HASH: 45c4542ba38743668115efef9ed09b52e7d9a2c2
                  MATRIX: 785->260|910->290|938->292|1018->397|1054->406|1089->414|1115->419|1204->481|1219->487|1274->521|1362->582|1377->588|1430->620|1482->645|1497->651|1542->675|1636->831|1672->840|3079->2220|3107->2227|3152->2244|3286->2351|3301->2357|3355->2390
                  LINES: 19->7|24->7|26->9|29->12|30->13|30->13|30->13|31->14|31->14|31->14|32->15|32->15|32->15|33->16|33->16|33->16|36->20|37->21|63->47|63->47|64->48|69->53|69->53|69->53
                  -- GENERATED --
              */
          
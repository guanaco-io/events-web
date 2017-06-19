
package views.html

import play.twirl.api._
import play.twirl.api.TemplateMagic._


     object index_Scope0 {
import models._;import controllers._;import play.api.templates.PlayMagic._;import play.api.i18n._;import play.api.mvc._;import play.api.data._;import views.html._;

class index extends BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with play.twirl.api.Template0[play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/():play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.4*/("""

"""),_display_(/*3.2*/main("Welcome to Play")/*3.25*/ {_display_(Seq[Any](format.raw/*3.27*/("""
  """),format.raw/*4.3*/("""<h1>Welcome to Play!</h1>
""")))}),format.raw/*5.2*/("""
"""))
      }
    }
  }

  def render(): play.twirl.api.HtmlFormat.Appendable = apply()

  def f:(() => play.twirl.api.HtmlFormat.Appendable) = () => apply()

  def ref: this.type = this

}


}

/**/
object index extends index_Scope0.index
              /*
                  -- GENERATED --
                  DATE: Tue May 23 20:56:41 CEST 2017
                  SOURCE: /home/gertv/Projects/anova/guanaco-web/app/views/index.scala.html
                  HASH: 05cc9606215463252c665a59a6bfcddc6c8045aa
                  MATRIX: 521->1|617->3|645->6|676->29|715->31|744->34|800->61
                  LINES: 14->1|19->1|21->3|21->3|21->3|22->4|23->5
                  -- GENERATED --
              */
          
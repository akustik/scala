package org.owing

import scala.swing.{ SimpleSwingApplication, MainFrame, Button, Component, Graphics2D }
import java.awt.{ Color, Dimension }
import java.awt.geom.Line2D
import org.owing.Cmd._
import org.owing.physics.{ Rect, Point }
import Math._

class GraphicalUI(games: List[Game], d: Dimension) extends SimpleSwingApplication {
  def x(v: Int)(implicit game: Game) = round(v.toDouble / game.w.toDouble * d.getWidth()).toInt
  def y(v: Int)(implicit game: Game) = (d.getHeight() - round(v.toDouble / game.h.toDouble * d.getHeight())).toInt
  def paintGame(g: Graphics2D, view: Game) = {
    implicit val game = view
    game.elements.foreach(e => {
      e.shape match {
        case r: Rect => {
          if(e.status.withCollision) g.setColor(new Color(255, 0, 0))
          else g.setColor(new Color(100, 100, 100))
          g.drawString(e.id + "(" + game.idx + ")" , x(r.topLeft.x), y(r.topLeft.y))
          g.draw(new Line2D.Double(x(r.topLeft.x), y(r.topLeft.y), x(r.topRight.x), y(r.topRight.y)))
          g.draw(new Line2D.Double(x(r.center.x), y(r.center.y), x(r.topRight.x), y(r.topRight.y)))
          g.draw(new Line2D.Double(x(r.topLeft.x), y(r.topLeft.y), x(r.center.x), y(r.center.y)))
          g.draw(new Line2D.Double(x(r.topLeft.x), y(r.topLeft.y), x(r.bottomLeft.x), y(r.bottomLeft.y)))
          g.draw(new Line2D.Double(x(r.bottomRight.x), y(r.bottomRight.y), x(r.topRight.x), y(r.topRight.y)))
          g.draw(new Line2D.Double(x(r.bottomRight.x), y(r.bottomRight.y), x(r.bottomLeft.x), y(r.bottomLeft.y)))            
        }
      }
    })
  }
  def top = new MainFrame {
    title = "o-wing"
    contents = new Component {
      preferredSize = d
      override def paintComponent(g: Graphics2D) = {
        super.paintComponent(g)
        games.foreach(paintGame(g, _))
      }
    }
  }
}

object Owing {
  def main(args: Array[String]) {
    println("Starting o-wing...")
    val initial = new Game(0, 1000, 1000)
    val g = initial.
      addStarShip("R1", Point(100, 100)).
      addStarShip("E1", Point(300, 600), 180).
      move("R1", Forward(3)).
      move("R1", RotateRight(2)).
      move("R1", Forward(5)).
      move("R1", RotateRight(2)).
      move("R1", RotateLeft(1)).
      move("R1", RotateLeft(1)).
      move("R1", Loop(5)).
      move("R1", TurnRight(2)).
      move("R1", Forward(1)).
      move("R1", TurnRight(5)).
      move("R1", RotateRight(5)).
      move("R1", Forward(3)).
      status
    val ui = new GraphicalUI(g, new Dimension(1000, 1000))
    ui.startup(args)
    println("Started")
  }
}

package org.owing

import scala.swing.{ SimpleSwingApplication, MainFrame, Button, Component, Graphics2D }
import java.awt.{ Color, Dimension }
import java.awt.geom.Line2D
import org.owing.Cmd._
import org.owing.physics.{ Rect, Point }
import Math._

class GraphicalUI(game: Game, d: Dimension) extends SimpleSwingApplication {
  def x(v: Int) = round(v.toDouble / game.w.toDouble * d.getWidth()).toInt
  def y(v: Int) = (d.getHeight() - round(v.toDouble / game.h.toDouble * d.getHeight())).toInt
  def top = new MainFrame {
    title = "o-wing"
    contents = new Component {
      preferredSize = d
      override def paintComponent(g: Graphics2D) = {
        super.paintComponent(g)
        g.setColor(new Color(100, 100, 100))
        game.elements.foreach(e => {
          e.shape match {
            case r: Rect => {
              g.drawString(e.id, x(r.topLeft.x), y(r.topLeft.y))
              g.draw(new Line2D.Double(x(r.topLeft.x), y(r.topLeft.y), x(r.topRight.x), y(r.topRight.y)))
              g.draw(new Line2D.Double(x(r.center.x), y(r.center.y), x(r.topRight.x), y(r.topRight.y)))
              g.draw(new Line2D.Double(x(r.topLeft.x), y(r.topLeft.y), x(r.center.x), y(r.center.y)))
              g.draw(new Line2D.Double(x(r.topLeft.x), y(r.topLeft.y), x(r.bottomLeft.x), y(r.bottomLeft.y)))
              g.draw(new Line2D.Double(x(r.bottomRight.x), y(r.bottomRight.y), x(r.topRight.x), y(r.topRight.y)))
              g.draw(new Line2D.Double(x(r.bottomRight.x), y(r.bottomRight.y), x(r.bottomLeft.x), y(r.bottomLeft.y)))            
              //g.drawRect(x(r.topLeft.x), y(r.topLeft.y), r.w, r.h)
            }
          }
        })
      }
    }
  }
}

object Owing {
  def main(args: Array[String]) {
    println("Starting o-wing...")
    val g = new Game(1000, 1000)
    g.addStarShip("Rebel1", Point(100, 100))
    g.addStarShip("Rebel1_1", Point(100, 100))
    g.addStarShip("Rebel1_2", Point(100, 100))
    g.addStarShip("Rebel1_3", Point(100,100))
    g.move("Rebel1_1", Forward(3))
    g.move("Rebel1_2", Forward(4))
    g.move("Rebel1_2", RotateRight(45)) 
    g.move("Rebel1_2", Forward(5))
    g.move("Rebel1_3", Forward(4))
    g.move("Rebel1_3", RotateRight(45))
    g.move("Rebel1_3", Forward(5))
    g.move("Rebel1_3", RotateRight(45))
    g.move("Rebel1_3", Forward(3))
    val ui = new GraphicalUI(g, new Dimension(800, 600))
    ui.startup(args)
    println("Started")
  }
}

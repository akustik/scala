package org.owing

import scala.swing.{ SimpleSwingApplication, MainFrame, Button, Component, Graphics2D }
import java.awt.{ Color, Dimension }
import org.owing.physics.{ Rect, Point }

class GraphicalUI(game: Game) extends SimpleSwingApplication {
  def height(v: Int) = game.h - v
  def top = new MainFrame {
    title = "o-wing"
    contents = new Component {
      preferredSize = new Dimension(game.w, game.h)
      override def paintComponent(g: Graphics2D) = {
        super.paintComponent(g)
        g.setColor(new Color(100, 100, 100))
        game.elements.foreach(e => {
          e.shape match {
            case r: Rect => {
              g.drawString(e.id, r.topLeft.x, height(r.topLeft.y))
              g.drawRect(r.topLeft.x, height(r.topLeft.y), r.w, r.h)
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
    val g = new Game(800, 600)
    g.addStarShip("Rebel1", Point(100, 100))
    val ui = new GraphicalUI(g)
    ui.startup(args)
    println("Started")
  }
}
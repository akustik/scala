package behavioral

import scala.collection.mutable.Map
import scala.collection.mutable.LinkedList
import scala.collection.mutable.ArrayStack

object InterpreterPattern {

  trait Expression {
    def interpret(c: Context): Int
  }

  class Plus(left: Expression, right: Expression) extends Expression {
    def interpret(c: Context): Int = {
      left.interpret(c) + right.interpret(c)
    }
  }

  class Literal(value: Int) extends Expression {
    def interpret(c: Context): Int = {
      value
    }
  }

  class Variable(name: String) extends Expression {
    def interpret(c: Context): Int = {
      c.getValue(name)
    }
  }

  class Context(values: Map[String, Int]) {
    def getValue(name: String) = values.getOrElse(name, 0)
  }

  class Evaluator(text: String) extends Expression {

    val exp = buildTree(text.split(" "), new ArrayStack[Expression])

    def interpret(c: Context): Int = {
      exp.interpret(c)
    }

    def buildTree(tokens: Array[String], tree: ArrayStack[Expression]): Expression = {
      if (tokens.size > 0) {
        tokens.head match {
          case "+" => tree += new Plus(tree.pop(), tree.pop())
          case x if x.forall(Character.isDigit(_)) => tree += new Literal(x.toInt)
          case x: String => tree += new Variable(x)
        }
        buildTree(tokens.tail, tree)
      } else {
        tree.pop()
      }
    }

    def name = text
  }

  class CommandContext {

  }

  trait CommandExpression {
    protected var args = Array[String]();
    def interpret(c: CommandContext)
    def addArgument(arg: String) = args = args :+ arg
    def name(): String
    def parent(): CommandExpression = this
  }

  class PrintCommand extends CommandExpression {
    def interpret(c: CommandContext) = println(args.mkString(" "))
    def name(): String = "PRINT"
  }

  class ErrorCommand extends CommandExpression {
    def interpret(c: CommandContext) = Console.err.println(args.mkString(" "))
    def name(): String = "ERROR"
  }
  
  class ArgumentCommand(parent: CommandExpression, arg: String) extends CommandExpression {
    parent().addArgument(arg)
    override def parent(): CommandExpression = findArgumentCommand(parent)
    def findArgumentCommand(parent: CommandExpression): CommandExpression = {
      if(parent.name != this.name()) parent     
      else findArgumentCommand(parent.parent())
    }
    def interpret(c: CommandContext) = {}
    def name(): String = "#ARGUMENT#"  
  }

  class CommandEvaluator(text: String) extends CommandExpression {
    private val availableCommands = List(new PrintCommand(), new ErrorCommand())
    def name(): String = "EVALUATE"
    def interpret(c: CommandContext) = {
      val commands = build(text.split(" "), Nil)
      commands.map(_.interpret(c))
    }

    def build(tokens: Array[String], commands: List[CommandExpression]): List[CommandExpression] = {
      if (tokens.size == 0) {
        commands
      } else {
        build(tokens.tail,
            commands :+ availableCommands.find(_.name() == tokens.head).
            getOrElse(new ArgumentCommand(commands.last, tokens.head)))       
      }
    }
  }

  /**
   * The INTERPRET pattern defines an expression interface that may be interpreted
   * by the client and sets up the grammar with the relation of the expressions. The
   * interpreter is able then to execute the expression with its tree.
   */

  def main(args: Array[String]): Unit = {
    val eval = new Evaluator("a b +")
    val ctx = new Context(Map("a" -> 2, "b" -> 3))
    println(eval.name + " = " + eval.interpret(ctx))

    val ctx2 = new CommandContext
    val eval2 = new CommandEvaluator("PRINT print this message ERROR this other message")
    eval2.interpret(ctx2)
  }

}
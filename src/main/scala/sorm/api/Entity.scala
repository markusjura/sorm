package sorm.api

import reflect.basis._

import sorm._
import reflection._
import structure._
import extensions.Extensions._

sealed case class Entity
  [ T : TypeTag ]
  ( indexes       : Set[Seq[String]] = Set(),
    uniqueKeys    : Set[Seq[String]] = Set() )
  {

    lazy val reflection
      = Reflection[T]
    def settings
      = EntitySettings(indexes, uniqueKeys)


    //  Validate input:
    {
      {
        def allDescendantGenerics
          ( r : Reflection )
          : Stream[Reflection]
          = r +:
            r.generics.view
              .flatMap{allDescendantGenerics}
              .toStream

        reflection.properties.values
          .flatMap{ allDescendantGenerics }
          .filter{ _ inheritsFrom Reflection[Option[_]] }
          .foreach{ r =>
            require( !r.generics(0).inheritsFrom(Reflection[Option[_]]),
                     "Type signatures with `Option` being directly nested in another `Option`, i.e. `Option[Option[_]]` are not allowed" )
            require( !r.generics(0).inheritsFrom(Reflection[Traversable[_]]),
                     "Type signatures with collection being directly nested in `Option`, e.g. `Option[Seq[_]]` are not allowed" )
          }
      }

      ( indexes.view ++ uniqueKeys.view )
        .flatten
        .foreach{ p =>
          require( reflection.properties.contains(p),
                   "Inexistent property: `" + p + "`" )
        }

      ( indexes.view ++ uniqueKeys.view )
        .foreach{ ps =>
          require( ps.view.distinct.size == ps.size,
                   "Not a distinct properties list: `" + ps.mkString(", ") + "`" )
        }
    }
  }
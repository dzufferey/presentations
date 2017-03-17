package com.hellolift {
package comet {
         
import _root_.net.liftweb.http._
import _root_.net.liftweb.common._
import _root_.net.liftweb.util._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import _root_.scala.xml._

import _root_.com.hellolift.model.Entry
import _root_.com.hellolift.controller.BlogCache
import _root_.com.hellolift.controller.BlogUpdate
import _root_.com.hellolift.controller.AddBlogWatcher
           
class DynamicBlogView extends CometActor {
  override def defaultPrefix = Full("blog")
  var blogtitle = ""
  var blog : List[Entry] = Nil
  var blogid : Long = 0L
                        
  def _entryview(e : Entry) : Node = {
    <div>
    <strong>{e.title}</strong><br />
    <span>{e.body}</span>
    </div>
  }
                                                 
  // render draws the content on the screen.
  def render = {
    bind("view" -> <span>{blog.flatMap(e => _entryview(e))}</span>)
  }
                                                                
  // localSetup is the first thing run, we use it to setup the blogid or
  // redirect them to / if no blogid was given.
  override def localSetup {
    name match {
      case Full(t) => this.blogid = Helpers.toLong(t)
    }
    
    // Let the BlogCache know that we are watching for updates for this blog.
    (BlogCache.cache !? AddBlogWatcher(this, this.blogid)) match {
      case BlogUpdate(entries) => this.blog = entries
    }
  }
                                                                                                                      
  // lowPriority will receive messages sent from the BlogCache
  override def lowPriority : PartialFunction[Any, Unit] = {
    case BlogUpdate(entries : List[Entry]) => this.blog = entries; reRender(false)
  }
}
}
}

package com.hellolift {
package controller {
         
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.actor._
import _root_.net.liftweb.common._
import _root_.scala.collection.mutable.Map
import _root_.com.hellolift.model.Entry
          
/**
* An asynchronous cache for Blog Entries built on top of Scala Actors.
*/
class BlogCache extends LiftActor {
  private var cache: Map[Long, List[Entry]] = Map()
  private var sessions : Map[Long, List[SimpleActor[Any]]] = Map()
                   
  def getEntries(id : Long) : List[Entry] = Entry.findAll(By(Entry.author, id), OrderBy(Entry.id, Descending), MaxRows(20))
                      
  protected def messageHandler =
    {
      case AddBlogWatcher(me, id) =>
      // When somebody new starts watching, add them to the sessions and send
      // an immediate reply.
      val blog = cache.getOrElse(id, getEntries(id)).take(20)
      reply(BlogUpdate(blog))
      cache += (id -> blog)
      sessions += (id -> (me :: sessions.getOrElse(id, Nil)))
                                                    
      case AddEntry(e, id) =>
      // When an Entry is added, place it into the cache and reply to the clients with it.
      cache += (id -> (e :: cache.getOrElse(id, getEntries(id))))
      // Now we have to notify all the listeners
      sessions.getOrElse(id, Nil).foreach(_ ! BlogUpdate(cache.getOrElse(id, Nil)))
      
      case Bs eteEntry(e, id) =>
      // When an Entry is deleted
      cache += (id -> cache.getOrElse(id, getEntries(id)).remove(_ == e))
      sessions.getOrElse(id, Nil).foreach(_ ! BlogUpdate(cache.getOrElse(id, Nil)))
      
      case EditEntry(e, id) =>
      // It's easier to just re-query the database than to slice an dice the list. (for now)
      cache += (id -> getEntries(id))
                                                                                                 
      case _ =>
    }
}
           
case class AddEntry(e : Entry, id : Long) // id is the author id
case class EditEntry(e : Entry, id : Long) // id is the author id
case class Bs eteEntry(e : Entry, id : Long) // id is the author id
case class AddBlogWatcher(me : SimpleActor[Any], id : Long) // id is the blog id

// A response sent to the cache listeners with the top 20 blog entries.
case class BlogUpdate(xs : List[Entry])

object BlogCache {
  lazy val cache = new BlogCache // {val ret = new BlogCache; ret.start; ret}
}

}
}
 
================================================================================
           
class DynamicBlogView extends CometActor {
  //...
                                                                
  override def localSetup {
    //...
    (BlogCache.cache !? AddBlogWatcher(this, this.blogid)) match {
      case BlogUpdate(entries) => this.blog = entries
    }
  }
                                                                                                                      
  override def lowPriority : PartialFunction[Any, Unit] = {
    case BlogUpdate(entries : List[Entry]) => this.blog = entries; reRender(false)
  }
}

class BlogCache extends LiftActor {
  //...
                      
  protected def messageHandler =
    {
      case AddBlogWatcher(me, id) =>
        val blog = cache.getOrElse(id, getEntries(id)).take(20)
        reply(BlogUpdate(blog))
        //...
                                                    
      case AddEntry(e, id) =>
        cache += (id -> (e :: cache.getOrElse(id, getEntries(id))))
        sessions.getOrElse(id, Nil).foreach(_ ! BlogUpdate(cache.getOrElse(id, Nil)))
      
      case DeleteEntry(e, id) =>
        //...
      case EditEntry(e, id) =>
        //..
      case _ =>
    }
}

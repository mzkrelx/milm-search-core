/**************************************************************
  Source  : MilmSearchApplication.java
  Date    : 2011/03/07 10:30:47
**************************************************************/
package info.one.ideal.milm.search.wink;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * アプリケーション全体のリソースを管理するクラス。
 * アプリケーションに対して1つ定義される。
 * getClassesメソッドでは、リソースクラスを定義したら Set にクラスを追加する。
 * 
 * @author Mizuki Yamanaka
 */
public class MilmSearchApplication extends Application {

    /*
     * (非 Javadoc)
     * @see javax.ws.rs.core.Application#getClasses()
     */
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(MailResource.class);
		return classes;
	}
}

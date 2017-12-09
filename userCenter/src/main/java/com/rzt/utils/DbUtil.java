/**
 * 
 */
package com.rzt.utils;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

/**
 *
 * @author LYTG
 * @since 2017年10月30日 下午8:13:01
 */
public class DbUtil {
	
	/**
	 * @author LYTG
	 * @since 2017年10月30日 下午8:19:07
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> list(Query q){
		q.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return q.getResultList();
	}
	
	/**
	 * @author LYTG
	 * @since 2017年10月30日 下午8:27:18
	 * @param q
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> map(Query q){
		q.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return (Map<String, Object>) q.getSingleResult();
	}
	
	/**
	 * @author LYTG
	 * @since 2017年10月30日 下午8:43:44
	 * @param em
	 * @param sql
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> list(EntityManager em, String sql){
		Query q = em.createNativeQuery(sql);
		q.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return q.getResultList();
	}
	
	/**
	 * @author LYTG
	 * @since 2017年10月30日 下午8:44:32
	 * @param em
	 * @param sql
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> map(EntityManager em, String sql){
		Query q = em.createNativeQuery(sql);
		q.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return (Map<String, Object>) q.getSingleResult();
	}
	
	/**
	 * @author LYTG
	 * @since 2017年10月30日 下午8:49:31
	 * @param em
	 * @param sql
	 * @param objects
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> list(EntityManager em, String sql, Object...objects){
		Query q = em.createNativeQuery(sql);
		if(objects!=null&&objects.length>0){
			for(int i=0;i<objects.length;i++){
				q.setParameter(i+1, objects[i]);
			}
		}
		q.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return q.getResultList();
	}
	
	/**
	 * @author LYTG
	 * @since 2017年10月30日 下午8:50:20
	 * @param em
	 * @param sql
	 * @param objects
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> map(EntityManager em, String sql, Object...objects){
		Query q = em.createNativeQuery(sql);
		if(objects!=null&&objects.length>0){
			for(int i=0;i<objects.length;i++){
				q.setParameter(i+1, objects[i]);
			}
		}
		q.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return (Map<String, Object>) q.getSingleResult();
	}
	
}

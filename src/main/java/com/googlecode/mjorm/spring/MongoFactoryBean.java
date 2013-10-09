package com.googlecode.mjorm.spring;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import com.mongodb.Mongo;
import com.mongodb.MongoURI;

/**
 * {@link FactoryBean} for creating {@link Mongo} objects.
 */
public class MongoFactoryBean
	extends AbstractFactoryBean<Mongo> {

	private boolean closeOnDestroy;
	private String uri;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Mongo createInstance()
		throws Exception {
		return new Mongo(new MongoURI(uri));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> getObjectType() {
		return Mongo.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void destroyInstance(Mongo instance)
		throws Exception {
		if (closeOnDestroy) {
			instance.close();
		}
	}

	/**
	 * @param closeOnDestroy the closeOnDestroy to set
	 */
	public void setCloseOnDestroy(boolean closeOnDestroy) {
		this.closeOnDestroy = closeOnDestroy;
	}

	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

}

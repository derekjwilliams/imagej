/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2013 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package imagej.io;

import java.io.IOException;

import org.scijava.event.EventService;
import org.scijava.log.LogService;
import org.scijava.plugin.AbstractHandlerService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.Service;

/**
 * Default implementation of {@link IOService}.
 * 
 * @author Curtis Rueden
 */
@Plugin(type = Service.class)
public final class DefaultIOService
	extends AbstractHandlerService<String, IOPlugin<?>> implements IOService
{

	@Parameter
	private LogService log;

	@Parameter
	private EventService eventService;

	// -- IOService methods --

	@Override
	public IOPlugin<?> getOpener(final String source) {
		for (final IOPlugin<?> handler : getInstances()) {
			if (handler.supportsOpen(source)) return handler;
		}
		return null;
	}

	@Override
	public <D> IOPlugin<D> getSaver(final D data, final String destination) {
		for (final IOPlugin<?> handler : getInstances()) {
			if (handler.supportsSave(data, destination)) {
				@SuppressWarnings("unchecked")
				IOPlugin<D> typedHandler = (IOPlugin<D>) handler;
				return typedHandler;
			}
		}
		return null;
	}

	@Override
	public Object open(final String source) throws IOException {
		return getOpener(source).open(source);
	}

	@Override
	public void save(Object data, String destination) throws IOException {
		getSaver(data, destination).save(data, destination);
	}

	// -- HandlerService methods --

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Class<IOPlugin<?>> getPluginType() {
		return (Class) IOPlugin.class;
	}

	@Override
	public Class<String> getType() {
		return String.class;
	}

}

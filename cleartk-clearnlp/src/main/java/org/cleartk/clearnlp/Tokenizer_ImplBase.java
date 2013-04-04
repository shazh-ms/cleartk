/*
 * Copyright (c) 2011, Regents of the University of Colorado 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * Neither the name of the University of Colorado at Boulder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. 
 */
package org.cleartk.clearnlp;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import com.googlecode.clearnlp.engine.EngineGetter;
import com.googlecode.clearnlp.reader.AbstractReader;
import com.googlecode.clearnlp.tokenization.AbstractTokenizer;

public abstract class Tokenizer_ImplBase<WINDOW_TYPE extends Annotation> extends JCasAnnotator_ImplBase{
	public static final String DEFAULT_DICTIONARY_FILE_NAME= "dictionary-1.2.0.zip";

    public static final String PARAM_LANGUAGE_CODE = ConfigurationParameterFactory.createConfigurationParameterName(
    		Tokenizer_ImplBase.class,
    		"languageCode");
    
    @ConfigurationParameter(
    		description = "Language code for the tokenizer (default value=en).",
    		defaultValue= AbstractReader.LANG_EN)
    private String languageCode;
    
    public static final String PARAM_DICTIONARY_URI = ConfigurationParameterFactory.createConfigurationParameterName(
			Tokenizer_ImplBase.class,
			"dictionaryUri");
	
    @ConfigurationParameter(
        description = "This parameter provides the URI of the tokenizer dictionary file.")
    private URI dictionaryUri;

	
    private AbstractTokenizer tokenizer;
	
    
    /**
     * Convenience method for getting an Analysis Engine for ClearNLP's English tokenizer.
     */
    public static AnalysisEngineDescription getDescription() throws ResourceInitializationException {
    	return AnalysisEngineFactory.createPrimitiveDescription(Tokenizer_ImplBase.class); 
    }
    
      
    protected abstract Collection<WINDOW_TYPE> selectWindows(JCas jCas);
    
    protected abstract void createToken(JCas jCas, int begin, int end) throws InstantiationException, InvocationTargetException, IllegalAccessException;
 
    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException {
      super.initialize(context);
      try {
        URL dictionaryURL = (this.dictionaryUri == null)
            ? Tokenizer_ImplBase.class.getResource(DEFAULT_DICTIONARY_FILE_NAME).toURI().toURL()
                : this.dictionaryUri.toURL();
            this.tokenizer = EngineGetter.getTokenizer(languageCode, dictionaryURL.openStream());
      } catch (Exception e) {
        throw new ResourceInitializationException(e);
      }
    }
	

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
      for (WINDOW_TYPE window : this.selectWindows(jCas)) {
        String windowText = window.getCoveredText();
        int windowOffset = window.getBegin();
        List<String> tokens = tokenizer.getTokens(windowText);

        int offset = 0;
        for (String token : tokens) {
          int tokenBegin = windowText.indexOf(token, offset);
          int tokenEnd = tokenBegin + token.length();
          try {
            this.createToken(jCas, windowOffset + tokenBegin, windowOffset + tokenEnd);
          } catch (Exception e) {
            throw new AnalysisEngineProcessException(e);
          }
          offset = tokenEnd;
        }
      }
    }

}

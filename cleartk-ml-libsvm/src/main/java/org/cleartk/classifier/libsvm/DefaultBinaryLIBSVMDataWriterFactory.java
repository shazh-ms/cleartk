/** 
 * Copyright (c) 2009, Regents of the University of Colorado 
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

package org.cleartk.classifier.libsvm;

import java.io.IOException;

import org.cleartk.classifier.DataWriter;
import org.cleartk.classifier.encoder.features.BooleanEncoder;
import org.cleartk.classifier.encoder.features.FeatureVectorFeaturesEncoder;
import org.cleartk.classifier.encoder.features.NumberEncoder;
import org.cleartk.classifier.encoder.features.StringEncoder;
import org.cleartk.classifier.encoder.features.normalizer.EuclidianNormalizer;
import org.cleartk.classifier.encoder.features.normalizer.NameNumberNormalizer;
import org.cleartk.classifier.encoder.outcome.BooleanToBooleanOutcomeEncoder;
import org.cleartk.classifier.jar.DataWriterFactory_ImplBase;
import org.cleartk.classifier.jar.DefaultDataWriterFactory;
import org.cleartk.classifier.util.featurevector.FeatureVector;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;

/**
 * <br>
 * Copyright (c) 2009, Regents of the University of Colorado <br>
 * All rights reserved.
 * 
 * @author Philipp Wetzler
 * @deprecated Use {@link DefaultDataWriterFactory} with {@link BinaryLIBSVMDataWriter}.
 */

@Deprecated
public class DefaultBinaryLIBSVMDataWriterFactory extends
    DataWriterFactory_ImplBase<FeatureVector, Boolean, Boolean> {

  public static final String PARAM_CUTOFF = ConfigurationParameterFactory.createConfigurationParameterName(
      DefaultBinaryLIBSVMDataWriterFactory.class,
      "cutoff");

  @ConfigurationParameter(
      defaultValue = "5",
      description = "features that occur less than this number of times over the whole training set will not be encoded during testing")
  protected int cutoff = 5;

  public DataWriter<Boolean> createDataWriter() throws IOException {
    BinaryLIBSVMDataWriter dataWriter = new BinaryLIBSVMDataWriter(outputDirectory);

    if (!this.setEncodersFromFileSystem(dataWriter)) {
      NameNumberNormalizer normalizer = new EuclidianNormalizer();
      FeatureVectorFeaturesEncoder fe = new FeatureVectorFeaturesEncoder(cutoff, normalizer);
      fe.addEncoder(new NumberEncoder());
      fe.addEncoder(new BooleanEncoder());
      fe.addEncoder(new StringEncoder());
      dataWriter.setFeaturesEncoder(fe);

      dataWriter.setOutcomeEncoder(new BooleanToBooleanOutcomeEncoder());
    }

    return dataWriter;
  }

}

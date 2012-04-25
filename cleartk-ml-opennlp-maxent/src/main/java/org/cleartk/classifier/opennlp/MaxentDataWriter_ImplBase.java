/** 
 * Copyright (c) 2007-2011, Regents of the University of Colorado 
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
package org.cleartk.classifier.opennlp;

import java.io.File;
import java.io.IOException;
import java.util.List;

import opennlp.model.RealValueFileEventStream;

import org.cleartk.classifier.CleartkProcessingException;
import org.cleartk.classifier.encoder.features.BooleanEncoder;
import org.cleartk.classifier.encoder.features.NameNumber;
import org.cleartk.classifier.encoder.features.NameNumberFeaturesEncoder;
import org.cleartk.classifier.encoder.features.NumberEncoder;
import org.cleartk.classifier.encoder.features.StringEncoder;
import org.cleartk.classifier.jar.DataWriter_ImplBase;

/**
 * <br>
 * Copyright (c) 2007-2011, Regents of the University of Colorado <br>
 * All rights reserved.
 * 
 * <p>
 * 
 * Each line of the training data contains a label/result for the instance followed by a string
 * representation of each feature. Models trained with data generated by this class should use
 * RealValueFileEventStream. For relevant discussion, please see:
 * 
 * https://sourceforge.net/forum/forum.php?thread_id=1925312&forum_id=18385
 * 
 * 
 * 
 * @author Philip Ogren
 * @author Steven Bethard
 * @see RealValueFileEventStream
 */
public abstract class MaxentDataWriter_ImplBase<CLASSIFIER_BUILDER_TYPE extends MaxentClassifierBuilder_ImplBase<? extends MaxentClassifier_ImplBase<OUTCOME_TYPE>, OUTCOME_TYPE>, OUTCOME_TYPE>
    extends DataWriter_ImplBase<CLASSIFIER_BUILDER_TYPE, List<NameNumber>, OUTCOME_TYPE, String> {

  public MaxentDataWriter_ImplBase(File outputDirectory) throws IOException {
    super(outputDirectory);
    NameNumberFeaturesEncoder ftrsNcdr = new NameNumberFeaturesEncoder();
    ftrsNcdr.addEncoder(new NumberEncoder());
    ftrsNcdr.addEncoder(new BooleanEncoder());
    ftrsNcdr.addEncoder(new StringEncoder());
    this.setFeaturesEncoder(ftrsNcdr);
  }

  @Override
  public void writeEncoded(List<NameNumber> features, String outcome)
      throws CleartkProcessingException {
    if (outcome == null) {
      throw CleartkProcessingException.noInstanceOutcome(features);
    }
    this.trainingDataWriter.print(outcome);

    if (features.size() == 0) {
      trainingDataWriter.print(" null=0");
    }

    // write each of the string features, encoded, into the training data
    for (NameNumber nameNumber : features) {
      this.trainingDataWriter.print(' ');
      if (nameNumber.number.doubleValue() == 1.0)
        trainingDataWriter.print(nameNumber.name);
      else
        trainingDataWriter.print(nameNumber.name + "=" + nameNumber.number);

    }

    // complete the feature line
    this.trainingDataWriter.println();
  }

}

/**
 * Copyright 2015
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cau.cs.se.evaluation.architecture.jobs;

import com.google.common.base.Objects;
import de.cau.cs.se.evaluation.architecture.hypergraph.Hypergraph;
import de.cau.cs.se.evaluation.architecture.jobs.ComplexityAnalysisJob;
import de.cau.cs.se.evaluation.architecture.transformation.metrics.TransformationHypergraphMetrics;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

@SuppressWarnings("all")
public class MetricsSubJob extends Job {
  private ComplexityAnalysisJob parent;
  
  public MetricsSubJob(final String name, final ComplexityAnalysisJob parent) {
    super(name);
    this.parent = parent;
  }
  
  protected IStatus run(final IProgressMonitor monitor) {
    final TransformationHypergraphMetrics metrics = new TransformationHypergraphMetrics(monitor);
    Hypergraph subgraph = null;
    while ((!Objects.equal((subgraph = this.parent.getNextSubgraph()), null))) {
      {
        metrics.setSystem(subgraph);
        double _calculate = metrics.calculate();
        this.parent.deliverMetricsResult(_calculate);
      }
    }
    return Status.OK_STATUS;
  }
}
/*
 * Copyright 2020 Google LLC
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

package io.grpc.examples.wallet;

import static java.util.concurrent.TimeUnit.SECONDS;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.StatusRuntimeException;
import io.grpc.examples.wallet.stats.PriceRequest;
import io.grpc.examples.wallet.stats.PriceResponse;
import io.grpc.examples.wallet.stats.StatsGrpc;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.opencensus.common.Duration;
import io.opencensus.common.Scope;
import io.opencensus.contrib.grpc.metrics.RpcViews;
import java.util.Collection;
import java.util.List;

import io.opencensus.trace.Span;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.samplers.Samplers;
import io.opencensus.trace.config.TraceConfig;
import io.opencensus.trace.config.TraceParams;

import io.opencensus.exporter.stats.stackdriver.StackdriverStatsConfiguration;
import io.opencensus.exporter.stats.stackdriver.StackdriverStatsExporter;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter;

import io.opencensus.common.Duration;
import io.opencensus.common.Timestamp;
import io.opencensus.contrib.grpc.metrics.RpcViews;
import io.opencensus.exporter.metrics.util.IntervalMetricReader;
import io.opencensus.exporter.metrics.util.MetricExporter;
import io.opencensus.exporter.metrics.util.MetricReader;
import io.opencensus.metrics.LabelKey;
import io.opencensus.metrics.LabelValue;
import io.opencensus.metrics.Metrics;
import io.opencensus.metrics.export.Metric;
import io.opencensus.metrics.export.MetricDescriptor;
import io.opencensus.metrics.export.Point;
import io.opencensus.metrics.export.TimeSeries;
import io.opencensus.trace.SpanContext;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.config.TraceConfig;
import io.opencensus.trace.export.SpanData;
import io.opencensus.trace.export.SpanExporter;

/** A client for the gRPC Wallet example. */
public class Observability {
  public static class CustomMetricsExporter extends MetricExporter {
    private static final String EXAMPLE_STATS_EXPORTER = "ExampleStatsExporter";
    private final IntervalMetricReader intervalMetricReader;

    private CustomMetricsExporter() {
      IntervalMetricReader.Options.Builder options = IntervalMetricReader.Options.builder();
      MetricReader reader =
              MetricReader.create(
                      MetricReader.Options.builder()
                              .setMetricProducerManager(Metrics.getExportComponent().getMetricProducerManager())
                              .setSpanName(EXAMPLE_STATS_EXPORTER)
                              .build());
      intervalMetricReader = IntervalMetricReader.create(this, reader, options.setExportInterval(Duration.create(1, 0)).build());
    }
    /** Creates and registers the ExampleStatsExporter. */
    public static CustomMetricsExporter createAndRegister() {
      return new CustomMetricsExporter();
    }

    @Override
    public void export(Collection<Metric> metrics) {
      System.out.println("Exporting  metrics");
      for (Metric metric : metrics) {
        MetricDescriptor md = metric.getMetricDescriptor();
        MetricDescriptor.Type type = md.getType();
        System.out.println("Name: " + md.getName() + ", type: " + type);
        List<LabelKey> keys = md.getLabelKeys();
        StringBuilder keysSb = new StringBuilder("Keys: ");
        for (LabelKey k : keys) {
          keysSb.append(k.getKey() + " ");
        }
        System.out.println("Keys: " + keysSb);
//        StringBuilder sb = new StringBuilder();
//        sb.append("Seconds\tNanos\tValue\n");
        List<TimeSeries> tss = metric.getTimeSeriesList();
        for (TimeSeries ts : tss) {
          Timestamp start = ts.getStartTimestamp();
          System.out.println("Start " + start + "\n");
          List<LabelValue> lvs = ts.getLabelValues();
          StringBuilder lvSb = new StringBuilder("Keys: ");
          for (LabelValue v : lvs) {
            lvSb.append(v.getValue() + " ");
          }
          System.out.println("Label values: " + lvSb + "\n");
        }
//          for (Point p : ts.getPoints()) {
//            Timestamp t = p.getTimestamp();
//            long s = t.getSeconds();
//            long nanos = t.getNanos();
//            String line = s + "\t" + nanos + "\t" + p.getValue();
//            sb.append(line);
//          }
//          System.out.println("Timeseries to export:\n" + sb);
//        }
      }
    }
  }

  static void setup() {
	  try {
// Register all the gRPC views and enable stats
        RpcViews.registerAllGrpcViews();
	CustomMetricsExporter.createAndRegister();

        // Create the Stackdriver stats exporter
        StackdriverStatsExporter.createAndRegister(
                StackdriverStatsConfiguration.builder()
//                .setProjectId(gcpProjectId)
                .setExportInterval(Duration.create(5, 0))
                .build());

        // For demo purposes, always sample
        TraceConfig traceConfig = Tracing.getTraceConfig();
        traceConfig.updateActiveTraceParams(
                traceConfig.getActiveTraceParams()
                    .toBuilder()
                    .setSampler(Samplers.alwaysSample())
                    .build());

        // Create the Stackdriver trace exporter
        StackdriverTraceExporter.createAndRegister(
                StackdriverTraceConfiguration.builder()
//               .setProjectId(gcpProjectId)
                .build());
	  } catch (Exception e) {
		  throw new RuntimeException(e);
	  }
  }
}

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.ozone.freon;

import java.io.IOException;

import org.apache.hadoop.hdds.cli.GenericCli;
import org.apache.hadoop.hdds.cli.HddsVersionProvider;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.tracing.TracingUtil;
import org.apache.hadoop.hdds.utils.HddsServerUtil;
import org.apache.hadoop.ozone.freon.containergenerator.GeneratorDatanode;
import org.apache.hadoop.ozone.freon.containergenerator.GeneratorOm;
import org.apache.hadoop.ozone.freon.containergenerator.GeneratorScm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Ozone data generator and performance test tool.
 */
@Command(
    name = "ozone freon",
    description = "Load generator and tester tool for ozone",
    subcommands = {
        RandomKeyGenerator.class,
        OzoneClientKeyGenerator.class,
        OzoneClientKeyValidator.class,
        OzoneClientKeyRemover.class,
        OmKeyGenerator.class,
        OmBucketGenerator.class,
        OmBucketRemover.class,
        HadoopFsGenerator.class,
        HadoopNestedDirGenerator.class,
        HadoopDirTreeGenerator.class,
        HadoopFsValidator.class,
        SameKeyReader.class,
        S3KeyGenerator.class,
        DatanodeChunkGenerator.class,
        DatanodeChunkValidator.class,
        DatanodeBlockPutter.class,
        FollowerAppendLogEntryGenerator.class,
        ChunkManagerDiskWrite.class,
        LeaderAppendLogEntryGenerator.class,
        GeneratorOm.class,
        GeneratorScm.class,
        GeneratorDatanode.class,
        ClosedContainerReplicator.class},
    versionProvider = HddsVersionProvider.class,
    mixinStandardHelpOptions = true)
public class Freon extends GenericCli {

  public static final Logger LOG = LoggerFactory.getLogger(Freon.class);

  public Freon() {
    super(Freon.class);
  }

  @Option(names = "--server",
      description = "Enable internal http server to provide metric "
          + "and profile endpoint")
  private boolean httpServer = false;

  private final boolean interactive = System.console() != null;

  private FreonHttpServer freonHttpServer;
  private OzoneConfiguration conf;

  @Override
  public void execute(String[] argv) {
    conf = createOzoneConfiguration();
    HddsServerUtil.initializeMetrics(conf, "ozone-freon");
    TracingUtil.initTracing("freon", conf);
    super.execute(argv);
  }

  public void stopHttpServer() {
    if (freonHttpServer != null) {
      try {
        freonHttpServer.stop();
      } catch (Exception e) {
        LOG.error("Freon http server can't be stopped", e);
      }
    }
  }

  public void startHttpServer() {
    if (httpServer) {
      try {
        freonHttpServer = new FreonHttpServer(conf);
        freonHttpServer.start();
      } catch (IOException e) {
        LOG.error("Freon http server can't be started", e);
      }
    }

  }

  public static void main(String[] args) {
    new Freon().run(args);
  }

  public boolean isInteractive() {
    return interactive;
  }
}

/*
 * Copyright 2018 ThoughtWorks, Inc.
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

package com.thoughtworks.go.plugin.access.configrepo.contract.tasks;

import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public abstract class CRAbstractFetchTask extends CRTask {
    public static final String TYPE_NAME = "fetch";
    protected String pipeline;
    protected String stage;
    protected String job;
    @SerializedName("artifact_origin")
    protected ArtifactOrigin artifactOrigin;

    public CRAbstractFetchTask(String type,
                               ArtifactOrigin artifactOrigin) {
        super(type);
        this.artifactOrigin = artifactOrigin;
    }

    protected CRAbstractFetchTask(String pipeline, String stage, String job, String type) {
        super(type);
        this.pipeline = pipeline;
        this.stage = stage;
        this.job = job;
    }

    protected CRAbstractFetchTask(String stage,
                                  String job,
                                  String type,
                                  ArtifactOrigin artifactOrigin) {
        super(type);
        this.stage = stage;
        this.job = job;
        this.artifactOrigin = artifactOrigin;
    }

    public CRAbstractFetchTask(CRRunIf runIf, CRTask onCancel) {
        super(runIf, onCancel);
    }

    public String getPipelineName() {
        return pipeline;
    }

    public void setPipelineName(String pipeline) {
        this.pipeline = pipeline;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public ArtifactOrigin getArtifactOrigin() {
        return artifactOrigin;
    }

    public void setArtifactOrigin(ArtifactOrigin artifactOrigin) {
        this.artifactOrigin = artifactOrigin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CRAbstractFetchTask)) return false;
        if (!super.equals(o)) return false;

        CRAbstractFetchTask that = (CRAbstractFetchTask) o;

        if (pipeline != null ? !pipeline.equals(that.pipeline) : that.pipeline != null) return false;
        if (stage != null ? !stage.equals(that.stage) : that.stage != null) return false;
        if (job != null ? !job.equals(that.job) : that.job != null) return false;
        return artifactOrigin != null ? artifactOrigin.equals(that.artifactOrigin) : that.artifactOrigin == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (pipeline != null ? pipeline.hashCode() : 0);
        result = 31 * result + (stage != null ? stage.hashCode() : 0);
        result = 31 * result + (job != null ? job.hashCode() : 0);
        result = 31 * result + (artifactOrigin != null ? artifactOrigin.hashCode() : 0);
        return result;
    }

    @Override
    public String getLocation(String parent) {
        String myLocation = getLocation() == null ? parent : getLocation();
        String pipe = getPipelineName() != null ? getPipelineName() : "unknown pipeline";
        String stage = getStage() != null ? getStage() : "unknown stage";
        String job = getJob() != null ? getJob() : "unknown job";

        return String.format("%s; fetch artifacts task from %s %s %s", myLocation, pipe, stage, job);
    }

    public enum ArtifactOrigin {
        gocd {
            @Override
            public Class<? extends CRAbstractFetchTask> getArtifactTaskClass() {
                return CRFetchArtifactTask.class;
            }
        }, external {
            @Override
            public Class<? extends CRAbstractFetchTask> getArtifactTaskClass() {
                return CRFetchPluggableArtifactTask.class;
            }
        };

        public abstract Class<? extends CRAbstractFetchTask> getArtifactTaskClass();

        public static ArtifactOrigin getArtifactOrigin(String origin) {
            return Arrays.stream(values())
                    .filter(item -> item.toString().equals(origin))
                    .findFirst()
                    .orElseThrow(() -> new JsonParseException(String.format("Invalid artifact origin '%s' for fetch task.", origin)));
        }
    }
}

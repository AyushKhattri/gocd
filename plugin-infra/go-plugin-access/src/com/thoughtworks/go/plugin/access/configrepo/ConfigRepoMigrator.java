/*
 * Copyright 2017 ThoughtWorks, Inc.
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

package com.thoughtworks.go.plugin.access.configrepo;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ConfigRepoMigrator {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonMessageHandler1_0.class);

    public String migrate(String oldJSON, int targetVersion) {
        LOGGER.debug("Migrating to version {}: {}", targetVersion, oldJSON);

        Chainr transform = getTransformerFor(targetVersion);
        Object transformedObject = transform.transform(JsonUtils.jsonToMap(oldJSON));
        String transformedJSON = JsonUtils.toJsonString(transformedObject);

        LOGGER.debug("After migration to version {}: {}", targetVersion, transformedJSON);
        return transformedJSON;
    }

    private Chainr getTransformerFor(int targetVersion) {
        try {
            String targetVersionFile = String.format("/config-repo/migrations/%s.json", targetVersion);
            String transformJSON = IOUtils.toString(this.getClass().getResourceAsStream(targetVersionFile), "UTF-8");
            return Chainr.fromSpec(JsonUtils.jsonToList(transformJSON));
        } catch (Exception e) {
            throw new RuntimeException("Failed to migrate to version " + targetVersion, e);
        }
    }
}
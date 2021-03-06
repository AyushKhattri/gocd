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

import Page from "helpers/spa_base";
import {ElasticProfilesPage} from "views/pages/elastic_profiles";

//tslint:disable
const m                     = require("mithril");
const stream                = require("mithril/stream");
const ElasticProfilesWidget = require("views/elastic_profiles/elastic_profiles_widget");
const PluginInfos           = require("models/shared/plugin_infos");
const PageLoadError         = require("views/shared/page_load_error");

//tslint:enable

export class ElasticProfilesSPA extends Page {
  constructor() {
    super(ElasticProfilesPage);
  }
}

$(() => {
  const elasticProfilesContainer = $("#elastic-profiles");

  if (elasticProfilesContainer.get().length === 0) {
    return new ElasticProfilesSPA();
  }

  const onSuccess = (pluginInfos: any) => {
    const component = {
      view() {
        return (<ElasticProfilesWidget pluginInfos={stream(pluginInfos)}/>);
      }
    };

    m.mount($("#elastic-profiles").get(0), component);
  };

  const onFailure = () => {
    const component = {
      view() {
        return (<PageLoadError message="There was a problem fetching the elastic profiles"/>);
      }
    };
    m.mount($("#elastic-profiles").get(0), component);
  };

  PluginInfos.all(null, {type: "elastic-agent"}).then(onSuccess, onFailure);
});

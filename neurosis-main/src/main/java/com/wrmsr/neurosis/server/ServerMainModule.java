/*
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
package com.wrmsr.neurosis.server;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.wrmsr.neurosis.failureDetector.FailureDetector;
import com.wrmsr.neurosis.failureDetector.FailureDetectorModule;
import io.airlift.configuration.AbstractConfigurationAwareModule;
import io.airlift.discovery.client.ServiceDescriptor;

import javax.inject.Singleton;

import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import static com.google.common.base.Preconditions.checkState;
import static io.airlift.concurrent.Threads.daemonThreadsNamed;
import static io.airlift.discovery.client.DiscoveryBinder.discoveryBinder;
import static io.airlift.jaxrs.JaxrsBinder.jaxrsBinder;
import static java.util.concurrent.Executors.newScheduledThreadPool;

public class ServerMainModule
        extends AbstractConfigurationAwareModule
{
    @Override
    protected void setup(Binder binder)
    {
        ServerConfig serverConfig = buildConfigObject(ServerConfig.class);

        binder.install(new CoordinatorModule());

        if (serverConfig.isCoordinator()) {
            discoveryBinder(binder).bindHttpAnnouncement("neurosis-coordinator");
        }

        bindFailureDetector(binder, serverConfig.isCoordinator());

        jaxrsBinder(binder).bind(ThrowableMapper.class);

        // Determine the NodeVersion
        String neurosisVersion = serverConfig.getNeurosisVersion();
        if (neurosisVersion == null) {
            neurosisVersion = detectNeurosisVersion();
        }
        checkState(neurosisVersion != null, "neurosis.version must be provided when it cannot be automatically determined");

        // thread visualizer
        jaxrsBinder(binder).bind(ThreadResource.class);
    }

    private static String detectNeurosisVersion()
    {
        String title = NeurosisServer.class.getPackage().getImplementationTitle();
        String version = NeurosisServer.class.getPackage().getImplementationVersion();
        return ((title == null) || (version == null)) ? null : (title + ":" + version);
    }

    private static void bindFailureDetector(Binder binder, boolean coordinator)
    {
        // TODO: this is a hack until the coordinator module works correctly
        if (coordinator) {
            binder.install(new FailureDetectorModule());
            jaxrsBinder(binder).bind(NodeResource.class);
        }
        else {
            binder.bind(FailureDetector.class).toInstance(new FailureDetector()
            {
                @Override
                public Set<ServiceDescriptor> getFailed()
                {
                    return ImmutableSet.of();
                }
            });
        }
    }
}

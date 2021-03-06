/*
 *******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.eclipse.microprofile.lra.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Used on ({@link LRA} and {@link Compensate} annotations to indicate the
 * maximum time that the LRA or participant should remain active for.
 *
 * The business logic may wish to control how long an LRA should remain active
 * for before it becomes eligible for automatic cancellation by annotating methods
 * that start LRAs with the `@TimeLimit` annotation. For example, to indicate that
 * an LRA should automatically cancel after 100 milliseconds:
 *
 * <pre>
 *   {@literal @}GET
 *   {@literal @}Path("/doitASAP")
 *   {@literal @}Produces(MediaType.APPLICATION_JSON)
 *   {@literal @}TimeLimit(limit = 100, unit = TimeUnit.MILLISECONDS)
 *   {@literal @}LRA(value = LRA.Type.REQUIRED)
 *   public Response theClockIsTicking(
 *       {@literal @}HeaderParam(LRAClient.LRA_HTTP_HEADER) String lraId) {...}
 * </pre>
 *
 * Furthermore, the ability to compensate may be a transient capability of a
 * service so participants can also be timed out. When the time limit is reached
 * the LRA is cancelled and participants will be notified via their compensation
 * (ie the method annotated with `{@literal @}Compensate`). To set such a time limit add the
 * `{@literal @}TimeLimit` annotation to the `{@literal @}Compensate` method. An example of how a participant
 * could indicate that its' ability to compensate is limited to 100 milliseconds could be:
 *
 * <pre>
 *     {@literal @}PUT
 *     {@literal @}Path("/compensate")
 *     {@literal @}Compensate
 *     {@literal @}TimeLimit(limit = 100, unit = TimeUnit.MILLISECONDS)
 *     public Response completeWork({@literal @}HeaderParam(LRA_HTTP_HEADER) String lraId,
 *                                  String userData) { ... }
 * </pre>
 *
 * In this example afer 100 milliseconds has passed the implementation SHOULD
 * automatcially cancel the LRA which will result in this method being invoked.
 *
 * If the annotation is applied at the class level the timeout applies to any
 * method that starts an LRA or registers a participant.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface TimeLimit {
    /**
     * @return the period for which the LRA or participant will remain valid.
     * A value of zero indicates that it is always remain valid.
     *
     * For compensations the corresponding compensation (a method annotated with
     * {@link Compensate} in the same class) will be invoked if the time limit is
     * reached.
     */
    long limit() default 0;

    TimeUnit unit() default TimeUnit.SECONDS;
}

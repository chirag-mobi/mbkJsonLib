/*
 * .
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mobikwik.testcode.annotations;

import com.mobikwik.testcode.Mobikwik;
import com.mobikwik.testcode.MobikwikBuilder;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that indicates the version number since a member or a type has been present.
 * This annotation is useful to manage versioning of your Json classes for a web-service.
 *
 * <p>
 * This annotation has no effect unless you build {@link Mobikwik} with a
 * {@link MobikwikBuilder} and invoke
 * {@link MobikwikBuilder#setVersion(double)} method.
 *
 * <p>Here is an example of how this annotation is meant to be used:</p>
 * <pre>
 * public class User {
 *   private String firstName;
 *   private String lastName;
 *   &#64;Since(1.0) private String emailAddress;
 *   &#64;Since(1.0) private String password;
 *   &#64;Since(1.1) private Address address;
 * }
 * </pre>
 *
 * <p>If you created Mobikwik with {@code new Mobikwik()}, the {@code toJson()} and {@code fromJson()}
 * methods will use all the fields for serialization and deserialization. However, if you created
 * Mobikwik with {@code Mobikwik Mobikwik = new MobikwikBuilder().setVersion(1.0).create()} then the
 * {@code toJson()} and {@code fromJson()} methods of Mobikwik will exclude the {@code address} field
 * since it's version number is set to {@code 1.1}.</p>
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Since {
  /**
   * the value indicating a version number since this member
   * or type has been present.
   */
  double value();
}

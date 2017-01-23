/*
 * Copyright 2017 Futurice GmbH
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
package quickbeer.android.core.viewmodel;

public interface ViewModel {

    /**
     * Bind the ViewModel to its data model (typically modelled Observable View data).
     */
    void bindToDataModel();

    /**
     * Unbind the ViewModel from its data model.
     */
    void unbindDataModel();

    /**
     * Permanently dispose of any resources held.
     *
     * The instance cannot be reused after this operation.
     */
    void dispose();
}

/*
 * Copyright (C) 2014 Giuseppe Buzzanca
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.pidygb.slidinglayout.widget;

import android.view.View;

public interface SlideListener {

    /**
     * Notifies the start of the slide animation.
     *
     * @param slidingView The sliding view
     */
    public void onSlideStart(View slidingView);

    /**
     * Notifies the end of the slide animation.
     *
     * @param slidingView The sliding view
     */
    public void onSlideEnd(View slidingView);

}
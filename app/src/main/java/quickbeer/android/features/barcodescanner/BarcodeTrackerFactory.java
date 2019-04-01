/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package quickbeer.android.features.barcodescanner;

import androidx.annotation.NonNull;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import quickbeer.android.features.barcodescanner.ui.camera.GraphicOverlay;

import static io.reark.reark.utils.Preconditions.get;

/**
 * Factory for creating a tracker and associated graphic to be associated with a new barcode.  The
 * multi-processor uses this factory to create barcode trackers as needed -- one for each barcode.
 */
class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {

    @NonNull
    private final BarcodeResultListener barcodeResultListener;

    @NonNull
    private final GraphicOverlay<BarcodeGraphic> graphicOverlay;

    BarcodeTrackerFactory(@NonNull BarcodeResultListener barcodeResultListener,
                          @NonNull GraphicOverlay<BarcodeGraphic> barcodeGraphicOverlay) {
        this.graphicOverlay = get(barcodeGraphicOverlay);
        this.barcodeResultListener = get(barcodeResultListener);
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        barcodeResultListener.onBarcodeDetected(barcode);

        BarcodeGraphic graphic = new BarcodeGraphic(graphicOverlay);
        return new BarcodeGraphicTracker(graphicOverlay, graphic);
    }
}


/*
 * Copyright (C) 2018  Ian Buttimer
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.ianbuttimer.tidder.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ianbuttimer.tidder.data.ContentProviderResponse;
import com.ianbuttimer.tidder.event.AbstractEvent;
import com.ianbuttimer.tidder.reddit.BaseObject;
import com.ianbuttimer.tidder.reddit.Response;

/**
 * Interface for processing common events
 * @param <E> class of event object
 * @param <R> class of reddit response
 */

public interface ICommonEvents<E extends AbstractEvent<E>, R extends Response<? extends BaseObject<?>>> {

    ICommonEvents<E, R> getFactoryInstance();

    /**
     * Create a new Response result event
     * @param response  Response
     * @return  event object
     */
    @Nullable E newResponseResult(R response);

    /**
     * Create a new ContentProviderResponse Result event
     * @param response  Response
     * @return  event object
     */
    @Nullable <T extends ContentProviderResponse> E newCpResponseResult(T response);

    IAdditionalInfoBuilder<?> infoBuilder(@NonNull E event);

    @Nullable Bundle additionalInfoTag(@NonNull E event);

    @Nullable Bundle additionalInfoAll(@NonNull E event);

    interface IAdditionalInfoBuilder<B extends AbstractEvent.AdditionalInfoBuilder<B, ?>> {
        B tag();
        B all();
    }


    IAdditionalInfoExtractor<E, ?> infoExtractor(@NonNull E event, @Nullable Bundle bundle);

    interface IAdditionalInfoExtractor<E extends AbstractEvent<E>,
                        X extends AbstractEvent.AdditionalInfoExtractor<X, E>> {
        X tag();
        X all();
        E done();
    }

}

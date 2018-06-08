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

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ianbuttimer.tidder.R;

import java.text.MessageFormat;

import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.styles.Github;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ianbuttimer.tidder.utils.ColourUtils.TRANSPARENT;

public class HelpActivity extends AppCompatActivity {

    @BindView(R.id.markdown_view) MarkdownView mMarkdown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ButterKnife.bind(this);

        mMarkdown.addStyleSheet(new HelpStyle(this));
        mMarkdown.loadMarkdownFromAsset("help.md");

    }


    class HelpStyle extends Github {
        public HelpStyle(Context context) {
            super();
            // replace scroll up colour
            int resourceColour = context.getResources().getColor(R.color.colorAccent);
            int colour = Color.argb(Float.valueOf(TRANSPARENT).intValue(),
                            Color.red(resourceColour),Color.green(resourceColour), Color.blue(resourceColour));
            String property = context.getString(R.string.markdownview_scrollup_property);
            String value = MessageFormat.format(
                    context.getString(R.string.markdownview_scrollup_setting),
                    Integer.toHexString(colour));
            addRule(property, value);
        }
    }

}

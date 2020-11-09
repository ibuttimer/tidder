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
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;

import com.ianbuttimer.tidder.R;

import java.text.MessageFormat;

import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.styles.Github;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ianbuttimer.tidder.utils.ColourUtils.TRANSPARENT;

public class HelpActivity extends AppCompatActivity {

    @BindView(R.id.markdown_view) MarkdownView mMarkdown;

    @StringRes private static final int[][] sPropSetting = new int[][] {
            //          property                                setting
            new int[] { R.string.markdownview_scrollup_property, R.string.markdownview_scrollup_setting },
            new int[] { R.string.markdownview_paragraph_property, R.string.markdownview_text_align_setting},
            new int[] { R.string.markdownview_text_left_property, R.string.markdownview_text_align_setting},
            new int[] { R.string.markdownview_text_right_property, R.string.markdownview_text_align_setting},
            new int[] { R.string.markdownview_th_left_property, R.string.markdownview_text_align_setting},
            new int[] { R.string.markdownview_th_right_property, R.string.markdownview_text_align_setting},
            new int[] { R.string.markdownview_td_left_property, R.string.markdownview_text_align_setting},
            new int[] { R.string.markdownview_td_right_property, R.string.markdownview_text_align_setting},
            new int[] { R.string.markdownview_h1_property, R.string.markdownview_text_align_setting},
            new int[] { R.string.markdownview_h2_property, R.string.markdownview_text_align_setting},
            new int[] { R.string.markdownview_h3_property, R.string.markdownview_text_align_setting},
            new int[] { R.string.markdownview_h4_property, R.string.markdownview_text_align_setting},
            new int[] { R.string.markdownview_h5_property, R.string.markdownview_text_align_setting},
            new int[] { R.string.markdownview_h6_property, R.string.markdownview_text_align_setting},
            new int[] { R.string.markdownview_ul_property, R.string.markdownview_dir_setting},
            new int[] { R.string.markdownview_ol_property, R.string.markdownview_dir_setting},
    };
    private static final int PROP_INDEX = 0;
    private static final int SETTING_INDEX = 1;

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
            String alignLeft = getString(R.string.markdownview_text_align_left);
            String alignRight = getString(R.string.markdownview_text_align_right);
            String dir = getString(R.string.markdownview_dir);

            /* rather than applying these settings, a simple dir='rtl' on the body or container div tag
                would handle this but that requires a modification to the MarkdownView library
             */

            for (int[] setting : sPropSetting) {
                String property = context.getString(setting[PROP_INDEX]);
                String value = context.getString(setting[SETTING_INDEX]);
                switch (setting[PROP_INDEX]) {
                    case R.string.markdownview_scrollup_property:
                        value = MessageFormat.format(value, Integer.toHexString(colour));
                        break;
                    case R.string.markdownview_paragraph_property:
                    case R.string.markdownview_text_left_property:
                    case R.string.markdownview_th_left_property:
                    case R.string.markdownview_td_left_property:
                    case R.string.markdownview_h1_property:
                    case R.string.markdownview_h2_property:
                    case R.string.markdownview_h3_property:
                    case R.string.markdownview_h4_property:
                    case R.string.markdownview_h5_property:
                    case R.string.markdownview_h6_property:
                        value = MessageFormat.format(value, alignLeft);
                        break;
                    case R.string.markdownview_text_right_property:
                    case R.string.markdownview_th_right_property:
                    case R.string.markdownview_td_right_property:
                        value = MessageFormat.format(value, alignRight);
                        break;
                    case R.string.markdownview_ul_property:
                    case R.string.markdownview_ol_property:
                        value = MessageFormat.format(value, dir);
                        break;
                    default:
                        value = null;
                        break;
                }
                if (!TextUtils.isEmpty(property) || !TextUtils.isEmpty(value)) {
                    addRule(property, value);
                }
            }
        }
    }

}

package com.angel.black.baframework.ui.util;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.util.StringUtil;

/**
 * Created by KimJeongHun on 2016-05-04.
 */
public class ViewUtil {
    public static void setEditTextPhoneDash(final EditText edit) {
        edit.addTextChangedListener(new TextWatcher() {
            String mPrevText;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mPrevText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                BaLog.i();

                String str = s.toString();
                if(!str.equals(mPrevText)) {
                    if (StringUtil.isCellPhoneWithDash(str)) {
                        String phoneNumWithDash = StringUtil.convertPhoneNumWithDash(str);
                        edit.setText(phoneNumWithDash);
                        edit.setSelection(edit.length());
                    } else {
                        // 숫자 외의 문자가 들어간 경우
                        if(str.contains("-")) {
                            // 숫자와 "-" 만 들어간 경우
                            String strWithoutDash = str.replaceAll("-", "");
                            edit.setText(strWithoutDash);
                            edit.setSelection(edit.length());
                        }
                    }
                }
            }
        });
    }

    public static void setEditTextCount(final EditText edit, final TextView text) {
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text.setText("" + edit.getText().toString().length());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static void setEditTextMoney(final EditText edit) {
        edit.addTextChangedListener(new TextWatcher() {
            private String mText;

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();

                if (!str.equals(mText)) {
                    mText = StringUtil.getMoney(str);
                    edit.setText(mText);
                    Editable e = edit.getText();

                    int len = mText.length();
                    Selection.setSelection(e, len);
                }
            }
        });
    }

    public static void setVisibilityHierancy(ViewGroup vg, int visiblity) {
        vg.setVisibility(visiblity);

        for(int i = 0 ; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            child.setVisibility(visiblity);

            if(child instanceof ViewGroup) {
                setVisibilityHierancy((ViewGroup) child, visiblity);
            }
        }
    }

    /**
     * 리스트 스크롤뷰를 제일 아래로 내린다.
     */
    public static void scrollToEnd(final ListView listView) {
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(listView.getCount() - 1);
            }
        }, 100);
    }

    /**
     * 키보드의 오른쪽 아래(완료, 검색) 버튼 누를시 특정 버튼의 클릭동작을 수행하도록 셋팅한다.
     */
    public static void setEditorActionForButtonClick(EditText editText, final Button btn) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_SEARCH:
                        btn.performClick();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    /**
     * 주어진 에딧텍스트가 포커스 될 때 입력된 문자열을 선택 상태로 만든다.
     */
    public static void setEditTextOnFocusSelectAll(final EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    editText.selectAll();
                }
            }
        });
    }
}

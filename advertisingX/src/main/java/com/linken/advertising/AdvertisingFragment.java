package com.linken.advertising;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.linken.advertising.net.ApiException;
import com.linken.advertising.net.AsyncHttpClient;
import com.linken.advertising.net.JsonObjectResponseHandler;
import com.linken.advertising.net.RequestBase;
import com.linken.advertising.utils.ToastUtils;
import com.linken.advertising.widget.LiteWebView;
import com.linken.advertising.widget.SimpleWebChromeClient;

import org.json.JSONObject;


public class AdvertisingFragment extends Fragment implements SimpleWebChromeClient.OnProgressCallback, LiteWebView.PageLoadListener {
    private LiteWebView mWebView;
    private ImageView mPrevBtn;
    private ImageView mNextBtn;

    private ImageView mCollect;
    private ImageView mBrowser;
    private View mAdvertisingLayout;
    private String mId;
    private String mUrl;
    private int mReadSecond;
    private ProgressBar mProgress;
    private boolean isPageLoadFinished;
    private AdvertisingSDK.IAdvertisingListener mListener;
    private AdvertisingSDK.IADCollectListener mAdCollectListener;
    private boolean isPause = false;//是否暂停
    private boolean isCountDown = false;//是否正在倒计时

    public static AdvertisingFragment newInstance() {
        AdvertisingFragment fragment = new AdvertisingFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isPause) {
                return;
            }
            mReadSecond--;
            if (mListener != null) {
                mListener.onCountDown(mReadSecond);
            }
            if (mReadSecond > 0) {
                AdvertisingFragment.this.sendMessage();
            } else {
                adComplete(mId);
            }
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_advertising, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListener = AdvertisingSDK.getInstance().getAdvertisingListener();
        mAdCollectListener = AdvertisingSDK.getInstance().getAdCollectListener();
        initViews(view);
        initWebView();
        distributead();
    }

    private void initViews(View view) {
        mPrevBtn = view.findViewById(R.id.prev_btn);
        mNextBtn = view.findViewById(R.id.next_btn);
        mWebView = view.findViewById(R.id.webView);
        mProgress = view.findViewById(R.id.progressBar);
        mAdvertisingLayout = view.findViewById(R.id.layout);
        mCollect = view.findViewById(R.id.collect);
        mBrowser = view.findViewById(R.id.browser);
        mPrevBtn.setEnabled(false);
        mNextBtn.setEnabled(false);
        mPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                }
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView.canGoForward()) {
                    mWebView.goForward();
                }
            }
        });

        mCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                commit();
                if(mAdCollectListener != null) {
                    mAdCollectListener.onCollectClick(mId,mCollect);
                }
            }
        });
        mBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mUrl)) {
                    return;
                }
                Uri uri = Uri.parse(mUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

    }

    private void distributead() {
        new AsyncHttpClient().get(SDKContants.URL_DISTRIBUTEAD, new JsonObjectResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response != null) {
                    if (response.has("data")) {
                        JSONObject jsonObject = response.optJSONObject("data");
                        if (jsonObject != null) {
                            mId = jsonObject.optString("id");
//                            mAdId = jsonObject.optString("adId");
                            mUrl = jsonObject.optString("url");
                            mReadSecond = jsonObject.optInt("readSecond");
                            onAistributeadSuccess();

//                            hasDone(mId);
                            if(mAdCollectListener != null) {
                                mAdCollectListener.showCollect(mId,mCollect);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
                //收益上线
                if (e instanceof ApiException && ((ApiException) e).getErrorCode() == 9991 && mListener != null) {
                    mAdvertisingLayout.setVisibility(View.GONE);
                    mListener.onAdvertisingLimit(((ApiException) e).getReaseon() + "");
                } else {
                    ToastUtils.showShort(getContext(), e.getMessage() + "");
                    getActivity().finish();
                }

            }
        });
    }


    private void adComplete(final String mId) {
        if (TextUtils.isEmpty(mId) || mId.trim().equals("")) {
            return;
        }
        RequestBase requestBase = new RequestBase() {
            @Override
            protected String getPath() {
                return SDKContants.URL_COMPLETE_AD;
            }

            @Override
            public String getURI() {
                String baseUrl = super.getURI();
                StringBuilder builder = new StringBuilder(baseUrl);
                builder.append("/");
                builder.append(mId);
                return builder.toString();
            }
        };
        new AsyncHttpClient().get(requestBase, new JsonObjectResponseHandler() {
            @Override
            public void onFailure(Throwable e) {
                if (mListener != null) {
                    mListener.onAdvertisingSucceed(false, mId, mAdvertisingLayout, e);
                }
            }

            @Override
            public void onSuccess(JSONObject response) {
                if (mListener != null) {
                    mListener.onAdvertisingSucceed(true, mId, mAdvertisingLayout, null);
                }
            }
        });
    }

    private void onAistributeadSuccess() {
        loadUrl(mUrl);
    }

    /**
     * 倒计时
     */
    private void sendMessage() {
        Message message = handler.obtainMessage();
        handler.sendMessageDelayed(message, 1000);
        isCountDown = true;
    }

    /**
     * 初始化webview
     */
    private void initWebView() {
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.init();
        mWebView.setChromeClientCallback(this);
        mWebView.setReloadUrlListener(new LiteWebView.ReloadUrlListener() {
            @Override
            public boolean reloadUrl() {
                mWebView.loadUrl(mUrl);
                return true;
            }
        });
        mWebView.setPageLoadListener(this);

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadUrl(String url) {
        if (mWebView != null) {
            mWebView.loadUrl(url);
        }
    }

    @Override
    public void onProgress(int progress) {
        mProgress.setVisibility(View.VISIBLE);
        if (progress > 98) {
            this.mProgress.setVisibility(View.GONE);
        } else {
            mProgress.setProgress(progress);
        }
    }

    @Override
    public void onPageLoadFinished() {
        if (!isPageLoadFinished) {
            isPageLoadFinished = true;
            sendMessage();
        }
        //显示上一个控件显示
        if (mWebView.canGoBack()) {
            mPrevBtn.setEnabled(true);
        } else {
            mPrevBtn.setEnabled(false);
        }
        if (mWebView.canGoForward()) {
            mNextBtn.setEnabled(true);
        } else {
            mNextBtn.setEnabled(false);
        }
    }


    @Override
    public void onPause() {
        if (mWebView != null) {
            if (Build.VERSION.SDK_INT >= 11) {
                if (mWebView != null) {
                    mWebView.onPause();
                }
            }
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.clearHistory();
            mWebView.clearCache(true);
            mWebView.destroy();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
        isPause = false;
        if (isCountDown) {
            sendMessage();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isPause = true;
    }

}

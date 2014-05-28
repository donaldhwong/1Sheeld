package com.integreight.onesheeld;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.integreight.firmatabluetooth.Jodem;
import com.integreight.onesheeld.utils.ConnectionDetector;
import com.integreight.onesheeld.utils.HttpRequest;
import com.integreight.onesheeld.utils.OneShieldButton;
import com.integreight.onesheeld.utils.OneShieldTextView;
import com.integreight.onesheeld.utils.customviews.CircularProgressBar;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

public class FirmwareUpdatingPopup extends Dialog {
	public static boolean isOpened = false;
	private OneShieldButton upgradeBtn;
	private ProgressBar progress;
	CircularProgressBar downloadingProgress;
	private OneShieldTextView statusText;
	private OneShieldTextView progressTxt;
	private MainActivity activity;
	private RelativeLayout transactionSlogan;
	Jodem jodem;
	private Handler uIHandler = new Handler();
	private boolean isFailed = false;
	private boolean isChinese = false;
	private final String TRACKER_CATG = "Firmware Upgrade";

	public FirmwareUpdatingPopup(final MainActivity activity, boolean isChinese) {
		super(activity, android.R.style.Theme_Translucent_NoTitleBar);
		this.activity = activity;
		final Handler handler = new Handler();
		this.isChinese = isChinese;
		jodem = new Jodem(activity.getThisApplication().getAppFirmata()
				.getBTService(), new Jodem.JodemEventHandler() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					Log.e("TAG", "Exception", e);
				}
				isFailed = false;
				// activity.getThisApplication().getAppFirmata().returnAppToNormal();
				activity.getThisApplication().getAppFirmata().enableReporting();
				activity.getThisApplication().getAppFirmata()
						.setAllPinsAsInput();
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						FirmwareUpdatingPopup.this.setCancelable(true);
						statusText.setText("Done Successfully!");
						setUpgrade();
					}
				});
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						changeSlogan("Upgrade Firmware", COLOR.BLUE);
					}
				}, 1500);
				activity.getThisApplication()
						.getGaTracker()
						.send(MapBuilder.createEvent(TRACKER_CATG,
								"Installed the firmware update successfully",
								"", null).build());

			}

			@Override
			public void onProgress(final int totalBytes, final int sendBytes,
					int errorCount) {
				// TODO Auto-generated method stub
				Log.d("bootloader", "total:" + totalBytes + " sent:"
						+ sendBytes + " error:" + errorCount);
				handler.post(new Runnable() {

					@Override
					public void run() {
						int status = (int) ((float) sendBytes / totalBytes * 100);
						changeSlogan("Installing...", COLOR.BLUE);
						downloadingProgress.setProgress(status);
						progressTxt.setText(status + "%");
					}
				});

			}

			@Override
			public void onError(final String error) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						FirmwareUpdatingPopup.this.setCancelable(true);
						// activity.getThisApplication().getAppFirmata().returnAppToNormal();
						activity.getThisApplication().getAppFirmata()
								.enableReporting();
						activity.getThisApplication().getAppFirmata()
								.setAllPinsAsInput();
						activity.getThisApplication()
								.getGaTracker()
								.send(MapBuilder
										.createEvent(
												TRACKER_CATG,
												"Failed to install the firmware version",
												"", null).build());
					}
				});
			}

			@Override
			public void onTimout() {
				// TODO Auto-generated method stub
				handler.post(new Runnable() {

					@Override
					public void run() {
						FirmwareUpdatingPopup.this.setCancelable(true);
						// activity.getThisApplication().getAppFirmata().returnAppToNormal();
						activity.getThisApplication().getAppFirmata()
								.enableReporting();
						activity.getThisApplication().getAppFirmata()
								.setAllPinsAsInput();
						changeSlogan("1Sheeld not responding!", COLOR.RED);
						isFailed = true;
						setUpgrade();
					}
				});

			}
		});
		activity.getThisApplication()
				.getGaTracker()
				.send(MapBuilder.createAppView()
						.set(Fields.SCREEN_NAME, "Firmware Upgrade").build());
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.updating_firmware_view);
		isOpened = true;
		upgradeBtn = (OneShieldButton) findViewById(R.id.update);
		progress = (ProgressBar) findViewById(R.id.progressUpdating);
		downloadingProgress = (CircularProgressBar) findViewById(R.id.progressDownloading);
		progressTxt = (OneShieldTextView) findViewById(R.id.progressTxt);
		statusText = (OneShieldTextView) findViewById(R.id.updateStatusText);
		transactionSlogan = (RelativeLayout) findViewById(R.id.transactionSloganUpdating);
		setUpgrade();
		changeSlogan(
				activity.getResources().getString(R.string.upgradeFirmata),
				COLOR.BLUE);
		setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				isOpened = false;
				activity.getThisApplication().getAppFirmata()
						.returnAppToNormal();
				jodem.stop();
			}
		});
		activity.getThisApplication().getAppFirmata().enableBootloaderMode();
		super.onCreate(savedInstanceState);
	}

	private void showInstallationProgress() {
		upgradeBtn.setVisibility(View.INVISIBLE);
		progress.setVisibility(View.VISIBLE);
		downloadingProgress.setVisibility(View.INVISIBLE);
		progressTxt.setVisibility(View.INVISIBLE);
	}

	private void showDownloadingProgress() {
		upgradeBtn.setVisibility(View.INVISIBLE);
		progress.setVisibility(View.INVISIBLE);
		downloadingProgress.setProgress(0);
		downloadingProgress.setVisibility(View.VISIBLE);
		progressTxt.setText("");
		progressTxt.setVisibility(View.VISIBLE);
	}

	private void reloadData() {

		HttpRequest.getInstance().get(
				"http://www.1sheeld.com/api/firmware.json",
				new JsonHttpResponseHandler() {

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseBody, Throwable e) {
						((OneSheeldApplication) activity.getApplication())
								.setMajorVersion(-1);
						((OneSheeldApplication) activity.getApplication())
								.setMinorVersion(-1);
						super.onFailure(statusCode, headers, responseBody, e);
					}

					@Override
					public void onSuccess(JSONObject response) {
						try {
							System.err.println(response);
							((OneSheeldApplication) activity.getApplication())
									.setMajorVersion(Integer.parseInt(response
											.getString("major")));
							((OneSheeldApplication) activity.getApplication())
									.setMinorVersion(Integer.parseInt(response
											.getString("minor")));
							((OneSheeldApplication) activity.getApplication())
									.setVersionWebResult(response.toString());
							downloadFirmware();
							activity.getThisApplication()
									.getGaTracker()
									.send(MapBuilder.createEvent(TRACKER_CATG,
											"Got the server version", "", null)
											.build());
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							Log.e("TAG", "Exception", e);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							Log.e("TAG", "Exception", e);
						}
						super.onSuccess(response);
					}
				});
	}

	private byte[] binaryFile;

	private void downloadFirmware() {
		changeSlogan("Downloading...", COLOR.BLUE);
		if (activity.getThisApplication().getVersionWebResult() != null) {
			showInstallationProgress();
			try {
				HttpRequest.getInstance().get(
						new JSONObject(activity.getThisApplication()
								.getVersionWebResult()).get(
								isChinese ? "url_china" : "url").toString(),
						new BinaryHttpResponseHandler(new String[] {
								"application/octet-stream", "text/plain" }) {
							@Override
							public void onSuccess(byte[] binaryData) {
								// TODO
								// Auto-generated
								// method stub
								FirmwareUpdatingPopup.this.setCancelable(false);
								activity.getThisApplication().getAppFirmata()
										.prepareAppForSendingFirmware();
								activity.getThisApplication().getAppFirmata()
										.resetMicro();
								binaryFile = binaryData;
								showDownloadingProgress();
								jodem.send(binaryData, 4);
								if (!isFailed)
									changeSlogan("Installing...", COLOR.BLUE);
								else
									changeSlogan("Please, Press reset!",
											COLOR.BLUE);

								// progressBar.setProgress(0);
								activity.getThisApplication()
										.getGaTracker()
										.send(MapBuilder
												.createEvent(
														TRACKER_CATG,
														"Downloaded the firmware update",
														"", null).build());
								super.onSuccess(binaryData);
							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers, byte[] binaryData,
									Throwable error) {
								changeSlogan("Error Downloading!", COLOR.RED);
								setUpgrade();
								Log.d("bootloader", statusCode + "");
								activity.getThisApplication()
										.getGaTracker()
										.send(MapBuilder
												.createEvent(
														TRACKER_CATG,
														"Failed to download the firmware update",
														statusCode + "", null)
												.build());
								super.onFailure(statusCode, headers,
										binaryData, error);
							}

							@Override
							public void onProgress(int bytesWritten,
									int totalSize) {
								// TODO
								// Auto-generated
								// method stub
								// int value = (int) ((bytesWritten / (float)
								// totalSize) * 100);
								// downloadingProgress.setProgress(value);
								// downloadingProgress.setTitle(value + " %");
								super.onProgress(bytesWritten, totalSize);
							}
						});
			} catch (JSONException e) {
				Log.e("TAG", "Exception", e);
			}
		} else
			reloadData();
	}

	private void setUpgrade() {
		upgradeBtn.setText("Upgrade");
		upgradeBtn.setVisibility(View.VISIBLE);
		progress.setVisibility(View.INVISIBLE);
		progressTxt.setVisibility(View.INVISIBLE);
		downloadingProgress.setVisibility(View.INVISIBLE);
		upgradeBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ConnectionDetector.isConnectingToInternet(activity)) {
					if (binaryFile == null)
						downloadFirmware();
					else {
						FirmwareUpdatingPopup.this.setCancelable(false);
						activity.getThisApplication().getAppFirmata()
								.prepareAppForSendingFirmware();
						activity.getThisApplication().getAppFirmata()
								.resetMicro();
						showDownloadingProgress();
						jodem.send(binaryFile, 4);
						if (!isFailed)
							changeSlogan("Installing...", COLOR.BLUE);
						else
							changeSlogan("Please, Press reset!", COLOR.BLUE);

					}
				} else {
					changeSlogan("No Internet Connection", COLOR.RED);
				}
			}
		});
	}

	private void changeSlogan(final String text, final int color) {
		uIHandler.post(new Runnable() {

			@Override
			public void run() {
				statusText.setText(text);
				transactionSlogan.setBackgroundColor(color);
			}
		});
	}

	private final static class COLOR {
		public final static int RED = 0xff9B1201;
		// public final static int YELLOW = 0xffE79401;
		public final static int BLUE = 0xff0094C1;
		// public final static int ORANGE = 0xffE74D01;
	}
}

package quannk.srtsynctool.listeners;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import quannk.srtsynctool.SRTSyncTool;

public final class QuickMoveSelectedAdapter extends SelectionAdapter {
	/**
	 * 
	 */
	private final SRTSyncTool srtSyncTool;

	/**
	 * @param srtSyncTool
	 */
	public QuickMoveSelectedAdapter(SRTSyncTool srtSyncTool) {
		this.srtSyncTool = srtSyncTool;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		try {
			this.srtSyncTool.iSpeech = Integer.parseInt(this.srtSyncTool.quickMove.getText());
			if (this.srtSyncTool.iSpeech < 0) {
				this.srtSyncTool.iSpeech = 0;
			} else if (this.srtSyncTool.iSpeech > this.srtSyncTool.enFile.speechs.size() - 1) {
				this.srtSyncTool.iSpeech = this.srtSyncTool.enFile.speechs.size() - 1;
			}

			this.srtSyncTool.quickMove(this.srtSyncTool.iSpeech);
		} catch (NumberFormatException e1) {
			this.srtSyncTool.quickMove.setText(Integer.toString(this.srtSyncTool.iSpeech));
			return;
		}
	}
}
package de.dieklaut.camtool.operations;

public enum ExportType {
	FULL(98,90,-1,-1), BIG(95,85,2160,2160), MEDIUM(90,80,1440,1080), SMALL(75,70,1080,480);

	int imageQuality;
	int videoQuality;
	int maxImageDimension;
	int maxVideoDimension;

	ExportType(int imageQuality, int videoQuality, int maxImageDimension, int maxVideoDimension) {
		this.imageQuality = imageQuality;
		this.videoQuality = videoQuality;
		this.maxImageDimension = maxImageDimension;
		this.maxVideoDimension = maxVideoDimension;
	}
	
	public static ExportType get(String input){
		return valueOf(input.toUpperCase());
	}

	public int getImageQuality() {
		return imageQuality;
	}

	public int getVideoQuality() {
		return videoQuality;
	}

	public int getMaxImageDimension() {
		return maxImageDimension;
	}

	public int getMaxVideoDimension() {
		return maxVideoDimension;
	}
}
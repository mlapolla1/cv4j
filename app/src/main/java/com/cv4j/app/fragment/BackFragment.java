public class BackFragment {

	public void initData(Resources res) {
		Bitmap bitmap1 = BitmapFactory.decodeResource(res, R.drawable.test_project_target);
        targetImage.setImageBitmap(bitmap1);

        Bitmap bitmap2 = BitmapFactory.decodeResource(res, R.drawable.test_project_sample);
        sampleImage.setImageBitmap(bitmap2);

        CV4JImage cv4jImage = new CV4JImage(bitmap1);
        ColorProcessor colorProcessor = (ColorProcessor)cv4jImage.getProcessor();

        int w = colorProcessor.getWidth();
        int h = colorProcessor.getHeight();

        // 反向投影结果
        CV4JImage resultCV4JImage = new CV4JImage(w,h);
        ByteProcessor byteProcessor = (ByteProcessor)resultCV4JImage.getProcessor();

        // sample
        CV4JImage sample = new CV4JImage(bitmap2);
        ColorProcessor sampleProcessor = (ColorProcessor)sample.getProcessor();
	}

}
TW.Runtime.Widgets.pdfgenerator = function() {
	var thisWidget = this;
	var currentSelectedRowNumber = undefined;
	var numRows = 0;
	let PDForientation;
	let PDFformat;
	let imageScaleMode;
	let fileName;
	let centerVertically;

	this.runtimeProperties = function() {
		return {
			'needsDataLoadingAndError': false
		};
	};

	this.renderHtml = function() {
		var html = '<div class="widget-content widget-pdfgenerator"> </div>';
		return html;
	};

	this.afterRender = function() {
		PDForientation = this.getProperty('Orientation');
		PDFformat = this.getProperty('Format');
		imageScaleMode = this.getProperty('ImageScaleMode');
		fileName = this.getProperty('FileName');
		centerVertically = this.getProperty('CenterVertically');
	};

	this.handleSelectionUpdate = function(propertyName, selectedRows, selectedRowIndices) {
		if (propertyName == "Data") {

		}
	};
	async function capturePage() {

		const canvas = await html2canvas(document.body, {
			scale: 3,   // improves resolution
			useCORS: true                     // if loading cross-origin images
		});
		return canvas;
	}

	async function printCurrentPage() {
		// Dynamically import jsPDF (UMD build)
		const { jsPDF } = window.jspdf;
		// 1. Capture the page as img to preserve the layout
		// note html2canvas does not preserve 100% of the page as the browser displays it because it does not support all CSS attributes
		const canvas = await capturePage();
		const imgData = canvas.toDataURL('image/png');
		// 2. Create a PDF that has a specific ratio
		const pdf = new jsPDF({
			orientation: PDForientation,
			unit: 'px',
			format: PDFformat
		});
		let pdfPageWidth = pdf.getPageWidth();
		let pdfPageHeight = pdf.getPageHeight();
		// 3. Add the image to cover the entire page
		let currentDPI = estimateDPI();
		//pdf.addImage(imgData, 'PNG', 0, 0, pdf.getPageWidth(), pdf.getPageHeight());
		let imageWidth = (canvas.width / currentDPI) * 25.4 / 3;
		let imageHeight = (canvas.height / currentDPI) * 25.4 / 3;
		const aspectRatio = canvas.width / canvas.height;
		let yPos = 0;
		if (centerVertically) {
			//will only move the image if the height of the page is bigger than the generated image and if the image scale is not height (in this case already the image should fill vertically the page)
			if (pdfPageHeight > imageHeight && imageScaleMode != "height")
				yPos = (pdfPageHeight - imageHeight) / 2;
		}
		if (imageScaleMode == "width") {
			pdf.addImage(imgData, 'PNG', 0, yPos, pdfPageWidth, pdfPageWidth / aspectRatio);
		}
		else if (imageScaleMode == "height") {
			pdf.addImage(imgData, 'PNG', 0, yPos, pdfPageHeight * aspectRatio, pdfPageHeight);
		}
		pdf.save(fileName);
	}


	function estimateDPI() {
		const dpi = document.createElement('div');
		dpi.style.width = '1in';
		dpi.style.height = '1in';
		dpi.style.position = 'absolute';
		dpi.style.top = '-100%';
		document.body.appendChild(dpi);
		const dpiValue = dpi.offsetWidth;
		document.body.removeChild(dpi);
		return dpiValue;
	}

	this.serviceInvoked = function(serviceName) {
		switch (serviceName) {
			case 'GeneratePDF':
				printCurrentPage();
				break;
			default:
				TW.log.error('PDFGenerator widget error: unexpected serviceName invoked "' + serviceName + '"');
		}
	};



	this.updateProperty = function(updatePropertyInfo) {
		if (updatePropertyInfo.TargetProperty === "FileName") {
			fileName = updatePropertyInfo.SinglePropertyValue;
		} else if (updatePropertyInfo.TargetProperty === "MultiSelectRowNumbers") {


		}
	};

	this.beforeDestroy = function() {
		var domElementId = this.jqElementId;
		var widgetElement = this.jqElement;

		try {
			widgetElement.unbind();
		}
		catch (destroyErr) {
		}

		try {
			widgetElement.empty();
		}
		catch (destroyErr) {
		}
	};
};
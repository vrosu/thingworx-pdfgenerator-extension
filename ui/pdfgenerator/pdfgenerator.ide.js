TW.IDE.Widgets.pdfgenerator = function() {

	this.widgetIconUrl = function() {
		return "../Common/extensions/PDFGenerator/ui/pdfgenerator/pdfgenerator.icon.ide.png";
	};

	this.widgetProperties = function() {
		return {
			'name': 'pdfgenerator',
			'description': 'Widget that allows generating PDF exports of Mashups directly from browser.',
			'category': ['Common'],
		//	'iconImage':'../../../extensions/PDFGenerator/ui/pdfgenerator/pdfgenerator.icon.ide.png',
			'properties': {
				'Orientation': {
					'description': 'PDF file orientation',
					'isBindingTarget': false,
					'isEditable': true,
					'baseType': 'STRING',
					'defaultValue': 'landscape',
					'selectOptions': [
						{ 'value': 'portrait', 'text': 'Portrait' },
						{ 'value': 'landscape', 'text': 'Landscape' }
					],
					'warnIfNotBoundAsTarget': false
				},
				'Format': {
					'description': 'PDF file format (paper size). If a custom format is needed then the widget should be extended',
					'isBindingTarget': false,
					'isEditable': true,
					'baseType': 'STRING',
					'defaultValue': 'a4',
					'selectOptions': [
						{ value: 'a0', text: 'A0' }, { value: 'a1', text: 'A1' }, { value: 'a2', text: 'A2' },
						{ value: 'a3', text: 'A3' }, { value: 'a4', text: 'A4' }, { value: 'a5', text: 'A5' },
						{ value: 'a6', text: 'A6' }, { value: 'a7', text: 'A7' }, { value: 'a8', text: 'A8' },
						{ value: 'a9', text: 'A9' }, { value: 'a10', text: 'A10' },

						{ value: 'b0', text: 'B0' }, { value: 'b1', text: 'B1' }, { value: 'b2', text: 'B2' },
						{ value: 'b3', text: 'B3' }, { value: 'b4', text: 'B4' }, { value: 'b5', text: 'B5' },
						{ value: 'b6', text: 'B6' }, { value: 'b7', text: 'B7' }, { value: 'b8', text: 'B8' },
						{ value: 'b9', text: 'B9' }, { value: 'b10', text: 'B10' },

						{ value: 'c0', text: 'C0' }, { value: 'c1', text: 'C1' }, { value: 'c2', text: 'C2' },
						{ value: 'c3', text: 'C3' }, { value: 'c4', text: 'C4' }, { value: 'c5', text: 'C5' },
						{ value: 'c6', text: 'C6' }, { value: 'c7', text: 'C7' }, { value: 'c8', text: 'C8' },
						{ value: 'c9', text: 'C9' }, { value: 'c10', text: 'C10' },


						{ value: 'dl', text: 'DL' },
						{ value: 'letter', text: 'Letter' },
						{ value: 'government-letter', text: 'Government Letter' },
						{ value: 'legal', text: 'Legal' },
						{ value: 'junior-legal', text: 'Junior Legal' },
						{ value: 'ledger', text: 'Ledger' },
						{ value: 'tabloid', text: 'Tabloid' },
						{ value: 'credit-card', text: 'Credit Card' }
					],
					'warnIfNotBoundAsTarget': false
				},
				'ImageScaleMode':
				{
					'description': 'Image scale mode; note that aspect ratio will be kept the same.',
					'isBindingTarget': false,
					'isEditable': true,
					'baseType': 'STRING',
					'defaultValue': 'width',
					'selectOptions': [
						{ 'value': 'width', 'text': 'Scale to width' },
						{ 'value': 'height', 'text': 'Scale to height' }
					],
					'warnIfNotBoundAsTarget': false

				},
				'FileName':
				{
					'description': 'File name to be used for the generated PDF. Must not include extension - it is added automatically by JSPDF',
					'isBindingTarget': true,
					'isEditable': true,
					'baseType': 'STRING',
					'defaultValue': 'GeneratedPDF',
					'warnIfNotBoundAsTarget': false

				},
				'CenterVertically':
				{
					'description': 'Centers the screenshot vertically in the generated PDF',
					'isBindingTarget': false,
					'isEditable': true,
					'baseType': 'BOOLEAN',
					'defaultValue': true,
					'warnIfNotBoundAsTarget': false

				},

			}
		};
	};

	this.widgetServices = function() {
		return {
			'GeneratePDF': { 'warnIfNotBound': false }
		};
	};

	this.renderHtml = function() {
		var html = '';
		html += '<div class="widget-content widget-pdfgenerator">PDF generator widget - Invisible at runtime</div>';
		return html;
	};

	this.validate = function() {
		var result = [];

		return result;
	};
};
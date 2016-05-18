/*
 * Copyright 2011-2016 pdf4j.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.pdf4j.icepdf.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.DocFlavor;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.MediaName;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrintQuality;
//import javax.print.attribute.standard.Sides;
import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.util.Defs;

/**
 * java PrinterApplicaion -file "pdf file name" -printer "printer name"
 * -password "password to pdf file"
 *
 * Parameter details: -file "pdf file name", required field -printer "printer
 * name", Optional default printer used when ignored -password "password to pdf
 * file" Optional use only when required -help Print this usage help.
 *
 * @author ubala
 */
public class PrinterApplication {

    private static final Logger logger
            = Logger.getLogger(PrinterApplication.class.toString());

    static {
        Defs.setProperty("java.awt.headless", "true");
        Defs.setProperty("org.icepdf.core.scaleImages", "false");
        Defs.setProperty("org.icepdf.core.print.disableAlpha", "true");
        Defs.setProperty("javax.print.attribute.standard.Sides", "two-sided");
        // set the graphic rendering hints for speed, we loose quite a bit of quality
        // when converting to TIFF, so no point painting with the extra quality
        Defs.setProperty("org.icepdf.core.print.alphaInterpolation", "VALUE_ALPHA_INTERPOLATION_SPEED");
        Defs.setProperty("org.icepdf.core.print.antiAliasing", "VALUE_ANTIALIAS_ON");
        Defs.setProperty("org.icepdf.core.print.textAntiAliasing", "VALUE_TEXT_ANTIALIAS_OFF");
        Defs.setProperty("org.icepdf.core.print.colorRender", "VALUE_COLOR_RENDER_SPEED");
        Defs.setProperty("org.icepdf.core.print.dither", "VALUE_DITHER_DEFAULT");
        Defs.setProperty("org.icepdf.core.print.fractionalmetrics", "VALUE_FRACTIONALMETRICS_OFF");
        Defs.setProperty("org.icepdf.core.print.interpolation", "VALUE_INTERPOLATION_NEAREST_NEIGHBOR");
        Defs.setProperty("org.icepdf.core.print.render", "VALUE_RENDER_SPEED");
        Defs.setProperty("org.icepdf.core.print.stroke", "VALUE_STROKE_PURE");
    }

    /**
     * Attempts to Print PDF documents which are specified as application
     * arguments.
     *
     * @param filePath PDF file name to be printed
     */
    public static void printFile(String filePath) {
        printFile(filePath, null);
    }
        /**
     * Attempts to Print PDF documents which are specified as application
     * arguments.
     *
     * @param filePath      PDF file name to be printed
     * @param printerName   Target Pinter name
     */
    public static void printFile(String filePath, String printerName) {
        PrintService selectedService = null;
        if (filePath == null || filePath.trim().length() == 0) {
            System.out.println("Input file not found: " + filePath);
            return;
        }
        if (printerName == null || printerName.trim().length() == 0) {
            System.out.println("PrinterName is empty, using default printer");
            selectedService = PrintServiceLookup.lookupDefaultPrintService();
        } else {

            /**
             * Find Available printers
             */
            PrintService[] services
                    = PrintServiceLookup.lookupPrintServices(
                            DocFlavor.SERVICE_FORMATTED.PRINTABLE, null);
            //DocFlavor.INPUT_STREAM.PDF, null);

            int selectedPrinter = 0;
            if (services != null && services.length > 0) {
                for (int i = 0, max = services.length - 1; i <= max; i++) {
                    if (services[i].getName().equalsIgnoreCase(printerName)) {
                        System.out.println("Printer '" + printerName + "' "
                                + "index is " + i);
                        selectedPrinter = i + 1;
                    }
                }
            }
            if (!(selectedPrinter > 0 && selectedPrinter <= services.length)) {
                System.out.println("Printer '" + printerName + "' not found, "
                        + "exit form program ...!");
                System.exit(0);
            } else {
                selectedService = services[selectedPrinter - 1];
            }
        }
        // Open the document, create a PrintHelper and finally print the document
        Document pdf = new Document();

        try {
            if (selectedService == null) {
                throw new PrintException("No printer services selected");
            } else {
                System.out.println("Preparing file " + filePath + " to print on " + selectedService.getName());
            }
            pdf.setFile(filePath);
            // create a new print helper with a specified paper size and print
            // quality
            HashPrintRequestAttributeSet hp = new HashPrintRequestAttributeSet();
            //hp.add(Sides.TWO_SIDED_LONG_EDGE);
            hp.add(new Copies(1));
            hp.add(MediaSizeName.NA_LETTER);
            hp.add(PrintQuality.NORMAL);
            hp.add(new JobName("PrinterApplicaion", Locale.getDefault()));
            HashDocAttributeSet hd = new HashDocAttributeSet();
            hd.add(MediaName.NA_LETTER_WHITE);
            //hd.add(Sides.TWO_SIDED_LONG_EDGE);

            PrintHelper printHelper = new PrintHelper(null, pdf.getPageTree(), 0f,
                    hd, hp);
            printHelper.setupPrintService(selectedService, hp, true);
            System.out.println("Print job started, " + new Date());
            printHelper.print(new PrintJobWatcher());
            System.out.println("Print job completed, " + new Date());

        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "PDF file not found.", e);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error loading PDF file", e);
        } catch (PDFSecurityException e) {
            logger.log(Level.WARNING,
                    "PDF security exception, unspported encryption type.", e);
        } catch (PDFException e) {
            logger.log(Level.WARNING, "Error loading PDF document.", e);
        } catch (PrintException e) {
            logger.log(Level.WARNING, "Error Printing document.", e);
        } finally {
            pdf.dispose();
        }
    }

    public static void main(String[] args) {
        String LHELP = "-help";
        String LFILE = "-file";
        String LPRINT = "-printer";
        String LPWD = "-password";

        /**
         * Find Available printers
         */
        PrintService[] services
                = PrintServiceLookup.lookupPrintServices(
                        DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);

        String file = null, print = null, pwd = null;
        try {
            if (args != null && args.length > 0) {
                for (int idx = 0; idx < args.length; idx++) {
                    if (args[idx].equalsIgnoreCase(LHELP)) {
                        printHelp();
                        System.exit(0);
                    }
                    if (args[idx].equalsIgnoreCase(LFILE)) {
                        file = args[++idx];
                    }
                    if (args[idx].equalsIgnoreCase(LPRINT)) {
                        print = args[++idx];
                    }
                    if (args[idx].equalsIgnoreCase(LPWD)) {
                        pwd = args[++idx];
                    }
                }
            }
            System.out.printf("\nInput parameters::\nfile: %s, printer %s, password: %s \n", file, print, pwd);
            if (file == null) {
                throw new Exception("\nNo PDF input file found\n");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            printHelp();
        }
        printFile(file, print);
    }

    private static void printHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("java java -jar pdf4j-icepdf-print-x.x.x.jar ")
            .append("-file \"pdf file name\" ")
            .append("-printer \"printer name\" ")
            .append("-password \"password to pdf file\" \n\nParameter details:\n")
            .append("\t-file     \"pdf file name\", required field\n")
            .append("\t-printer  \"printer name\", Optional default printer used when ignored\n")
            .append("\t-password \"password to pdf file\" Optional use only when required\n")
            .append("\t-help      Print this usage help.\n");
        System.out.println(sb.toString());
    }
}

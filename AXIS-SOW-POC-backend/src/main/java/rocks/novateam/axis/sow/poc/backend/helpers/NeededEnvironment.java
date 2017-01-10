/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.backend.helpers;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import rocks.novateam.axis.sow.poc.backend.ontology.TDBManager;

/**
 *
 * @author Olivier Sailly
 */
public final class NeededEnvironment {
    private TDBManager mTDBManager;
    private Dataset mDataset;
    private Model mModel;
    private OntModel mOntModel;
    private ReadWrite mReadWrite;

    /**
     * Constructor of the class neededEnvironnement. This public method sets the
     * dataset, model, and ontmodel by default. It uses the ReadWrite entry to
     * know what to do.
     *
     * @param rw Tells whether to begin writing or reading
     */
    public NeededEnvironment(ReadWrite rw) {
        this.mTDBManager = TDBManager.getInstance();
        this.mReadWrite = rw;
        this.mDataset = mTDBManager.getDataset();
        this.mDataset.begin(this.mReadWrite);
        this.mModel = mDataset.getDefaultModel();
        this.mOntModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, mModel);
    }

    /**
     * Constructor of the class neededEnvironnement. This public method calls
     * the default constructor. It is able to set the OntModel's StrictMode.
     *
     * Note :
     *
     * setStrictMode() is set to false to solve the problem with the
     * ConversionException with classes as
     * ~/interoperabilitymodel/ontology/0.4#AXE
     *
     * As stated in http://stackoverflow.com/a/17447438/7358724 , we have to
     * call setStrictMode(false) on our OntModel, in order to be able to view
     * every resource as a class, by switching off strict checking.
     *
     * @param rw Tells whether to begin writing or reading
     * @param ontModelStrictMode Tells whether to set StrictMode to true or
     * false
     */
    public NeededEnvironment(ReadWrite rw, boolean ontModelStrictMode) {
        this(rw);
        this.mOntModel.setStrictMode(ontModelStrictMode);
    }

    /**
     * This public method should be used to finish any process. The method knows
     * whether to finish a READ or a WRITE, otherwise, it aborts everything.
     */
    public void finish() {
        switch (this.mReadWrite) {
            case READ:
                this.mDataset.end();
                break;
            case WRITE:
                this.mDataset.commit();
                break;
            default:
                this.mDataset.abort();
                break;
        }
    }

    public Dataset getDataset() {
        return mDataset;
    }

    public void setDataset(Dataset ds) {
        this.mDataset = ds;
    }

    public Model getModel() {
        return mModel;
    }

    public void setModel(Model mModel) {
        this.mModel = mModel;
    }

    public OntModel getOntModel() {
        return mOntModel;
    }

    public void setOntModel(OntModel mOntModel) {
        this.mOntModel = mOntModel;
    }
}

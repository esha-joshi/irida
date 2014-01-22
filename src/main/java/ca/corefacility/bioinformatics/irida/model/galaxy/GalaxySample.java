package ca.corefacility.bioinformatics.irida.model.galaxy;

import static com.google.common.base.Preconditions.*;

import java.io.File;
import java.util.List;

/**
 * Represents a Sample to be uploaded to Galaxy
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxySample
{
	private GalaxyObjectName sampleName;
	private List<File> sampleFiles;
	
	public GalaxySample(GalaxyObjectName sampleName, List<File> sampleFiles)
	{
		checkNotNull(sampleName, "sampleName is null");
		checkNotNull(sampleFiles, "sampleFiles is null");
		
		this.sampleName = sampleName;
		this.sampleFiles = sampleFiles;
	}
	
	public GalaxyObjectName getSampleName()
	{
		return sampleName;
	}
	public void setSampleName(GalaxyObjectName sampleName)
	{
		this.sampleName = sampleName;
	}
	public List<File> getSampleFiles()
	{
		return sampleFiles;
	}
	public void setSampleFiles(List<File> sampleFiles)
	{
		this.sampleFiles = sampleFiles;
	}

	@Override
    public String toString()
    {
	    return "GalaxySample [sampleName=" + sampleName + ", sampleFiles="
	            + sampleFiles + "]";
    }

	@Override
    public int hashCode()
    {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result
	            + ((sampleFiles == null) ? 0 : sampleFiles.hashCode());
	    result = prime * result
	            + ((sampleName == null) ? 0 : sampleName.hashCode());
	    return result;
    }

	@Override
    public boolean equals(Object obj)
    {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    GalaxySample other = (GalaxySample) obj;
	    if (sampleFiles == null)
	    {
		    if (other.sampleFiles != null)
			    return false;
	    } else if (!sampleFiles.equals(other.sampleFiles))
		    return false;
	    if (sampleName == null)
	    {
		    if (other.sampleName != null)
			    return false;
	    } else if (!sampleName.equals(other.sampleName))
		    return false;
	    return true;
    }
}

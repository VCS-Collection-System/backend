package com.redhat.vcs_kjar;

import java.io.Serializable;
import java.util.Objects;

public class AutomaticApprovalThreshold implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean isMet;

    public AutomaticApprovalThreshold() {
    }

    public AutomaticApprovalThreshold(boolean isMet) {
        this.isMet = isMet;
    }

    public boolean isMet() {
        return isMet;
    }

    public void setMet(boolean isMet) {
        this.isMet = isMet;
    }

    @Override
    public int hashCode() {
        final int prime = 31;

        int result = 1;
        result = prime * result + (isMet ? 1231 : 1237);

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        AutomaticApprovalThreshold other = (AutomaticApprovalThreshold) obj;

        return Objects.equals(isMet(), other.isMet());
    }

    @Override
    public String toString() {
        return "AutomaticApprovalThreshold ["
                + "isMet=" + isMet()
                + "]";
    }
}

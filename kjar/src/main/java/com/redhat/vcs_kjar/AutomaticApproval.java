package com.redhat.vcs_kjar;

import java.io.Serializable;
import java.util.Objects;

public class AutomaticApproval implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean isApplicable;

    public AutomaticApproval() {
    }

    public AutomaticApproval(boolean isApplicable) {
        this.isApplicable = isApplicable;
    }

    public boolean isApplicable() {
        return isApplicable;
    }

    public void setApplicable(boolean isApplicable) {
        this.isApplicable = isApplicable;
    }

    @Override
    public int hashCode() {
        final int prime = 31;

        int result = 1;
        result = prime * result + (isApplicable ? 1231 : 1237);

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

        AutomaticApproval other = (AutomaticApproval) obj;

        return Objects.equals(isApplicable(), other.isApplicable());
    }

    @Override
    public String toString() {
        return "AutomaticApproval ["
                +"isApplicable=" + isApplicable()
                + "]";
    }
}

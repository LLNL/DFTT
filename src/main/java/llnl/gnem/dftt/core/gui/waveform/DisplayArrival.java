/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.dftt.core.gui.waveform;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

import llnl.gnem.dftt.core.database.ConnectedUser;
import llnl.gnem.dftt.core.waveform.components.ComponentIdentifier;

/**
 *
 * @author dodge1
 */
public class DisplayArrival {

    protected double relTime;
    private final double refTime;
    private double deltim;
    private final String phase;
    private String auth;
    private final ComponentIdentifier identifier;
    private int arid;
    private int modificationCount;
    private boolean mutable;
    private boolean movable;
    private boolean canBeDeleted;
    private final Collection<ArrivalChangeListener> listeners;

    public DisplayArrival(double relTime,
            double refTime,
            double deltim,
            String phase,
            String auth,
            ComponentIdentifier identifier) {
        this(relTime, refTime, deltim, phase, auth, identifier, -1);
    }

    @Override
    public String toString() {
        String objectId = Integer.toHexString(System.identityHashCode(this));
        return String.format("DisplayArrival(%s): Arid=%d, Phase=%s, Reltime=%6.3f, Auth=%s ", objectId,arid, phase, relTime, auth);
    }

    public DisplayArrival(double relTime,
            double refTime,
            double deltim,
            String phase,
            String auth,
            ComponentIdentifier identifier,
            int arid) {
        this.relTime = relTime;
        this.refTime = refTime;
        this.deltim = deltim;
        this.phase = phase;
        this.auth = auth;
        this.identifier = identifier;
        this.arid = arid;
        mutable = true;
        movable = true;
        canBeDeleted = true;

        // New arrival has a modification count of 1, existing arrival has count of 0
        this.modificationCount = arid > 0 ? 0 : 1;

        listeners = new ArrayList<>();
    }

    public DisplayArrival(DisplayArrival other) {
        this(other.relTime, other.refTime, other.deltim, other.phase, other.auth, other.identifier, other.arid);
    }

    public DisplayArrival(DisplayArrival other, double delta) {
        this(other.relTime + delta, other.refTime, other.deltim, other.phase, ConnectedUser.getInstance().getUser(), other.identifier, -1);
    }

    public void updateFrom(DisplayArrival other) {
        if (!auth.equalsIgnoreCase(other.auth)) {
            auth = other.auth;
            arid = other.arid;
        }

        if (relTime != other.relTime) {
            setTime(other.relTime);
        }
        if (deltim != other.deltim) {
            setDeltim(other.deltim);
        }
    }

    public void addListener(ArrivalChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ArrivalChangeListener listener) {
        listeners.remove(listener);
    }

    public void setClean() {
        modificationCount = 0;
    }

    public void incrementModificationCount() {
        ++modificationCount;
    }

    public void decrementModificationCount() {
        --modificationCount;
    }

    public double getTime() {
        return getRelTime();
    }

    public double getRelTime() {
        return relTime;
    }

    public double getRefTime() {
        return refTime;
    }

    public double getEpochTime() {
        return refTime + relTime;
    }

    public double getDeltim() {
        return deltim;
    }

    public String getPhase() {
        return phase;
    }

    public boolean isDirty() {
        return modificationCount > 0;
    }

    public String getAuth() {
        return auth;
    }

    public ComponentIdentifier getIdentifier() {
        return identifier;
    }

    public boolean isMutable() {
        return mutable;
    }

    public Color getRenderColor() {
        return Color.black;
    }

    public void setDeltim(double deltim) {
        this.deltim = deltim;
        notifyListeners();
    }

    public void setTime(double pickTime) {
        relTime = pickTime;
        notifyListeners();
    }

    public void setMovable(boolean canMove) {
        movable = canMove;
    }

    public boolean isMovable() {
        return movable;
    }

    public boolean isCanBeDeleted() {
        return canBeDeleted;
    }

    public void setCanBeDeleted(boolean value)
    {
        canBeDeleted = value;
    }

    private void notifyListeners() {
        for (ArrivalChangeListener listener : listeners) {
            listener.arrivalChanged(this);
        }
    }

    public boolean hasArid() {
        return arid != -1;
    }

    public int getArid() {
        return arid;
    }

    public void setArid(int arid) {
        this.arid = arid;
    }

    protected void setAuth(String auth)
    {
        this.auth = auth;
    }
}

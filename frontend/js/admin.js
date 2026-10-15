// Zenith '26 — Admin Dashboard JavaScript Logic

let allEvents = [];
let allRegistrations = [];

// DOM Elements - Stats Cards
const statTotalEvents = document.getElementById("stat-total-events");
const statTotalRegistrations = document.getElementById("stat-total-registrations");
const statUtilizationPct = document.getElementById("stat-utilization-pct");
const statUtilizationBar = document.getElementById("stat-utilization-bar");
const statUpcomingEvents = document.getElementById("stat-upcoming-events");
const categoryBreakdownContainer = document.getElementById("category-breakdown-container");

// DOM Elements - Tabs & Tables
const tabEventsBtn = document.getElementById("tab-events-btn");
const tabRegistrationsBtn = document.getElementById("tab-registrations-btn");
const sectionEventsTable = document.getElementById("section-events-table");
const sectionRegistrationsTable = document.getElementById("section-registrations-table");
const eventsTabBadge = document.getElementById("events-tab-badge");
const regTabBadge = document.getElementById("reg-tab-badge");

const eventsTableBody = document.getElementById("events-table-body");
const registrationsTableBody = document.getElementById("registrations-table-body");
const filterRegEvent = document.getElementById("filter-reg-event");
const regCountInfo = document.getElementById("reg-count-info");
const refreshDashboardBtn = document.getElementById("refresh-dashboard-btn");

// Modal Elements - Add Event
const openAddEventModalBtn = document.getElementById("open-add-event-modal-btn");
const closeAddEventModalBtn = document.getElementById("close-add-event-modal-btn");
const addEventModal = document.getElementById("add-event-modal");
const addEventForm = document.getElementById("add-event-form");
const addEventAlert = document.getElementById("add-event-alert");
const submitAddEventBtn = document.getElementById("submit-add-event-btn");

// Category Dark-Neon Colors
const catColorMap = {
  Technical: "bg-cyan-950/80 text-cyan-300 border-cyan-500/30",
  Cultural: "bg-fuchsia-950/80 text-fuchsia-300 border-fuchsia-500/30",
  Sports: "bg-emerald-950/80 text-emerald-300 border-emerald-500/30",
  Gaming: "bg-amber-950/80 text-amber-300 border-amber-500/30",
  Workshop: "bg-violet-950/80 text-violet-300 border-violet-500/30",
};

// Fallback festival events dataset
const DEFAULT_FEST_EVENTS = [
  { id: 1, title: "Code Sprint 2.0", category: "Technical", description: "Fast-paced competitive coding contest. 3 rounds of algorithmic problem solving.", event_date: "2026-11-14", event_time: "10:00 AM", venue: "Lab 3, Computing Block", capacity: 100, fee: 0, icon: "💻", available_seats: 98 },
  { id: 2, title: "Robo Race", category: "Technical", description: "Obstacle course for autonomous and RC bots. Time-trial format with elimination rounds.", event_date: "2026-11-14", event_time: "02:00 PM", venue: "Open Amphitheatre", capacity: 30, fee: 150, icon: "🤖", available_seats: 28 },
  { id: 3, title: "Battle of the Bands", category: "Cultural", description: "College rock and fusion band competition. 20-minute live set per band.", event_date: "2026-11-14", event_time: "06:00 PM", venue: "Main Stage, Central Grounds", capacity: 500, fee: 0, icon: "🎸", available_seats: 498 },
  { id: 4, title: "Street Dance Battle", category: "Cultural", description: "1v1 and crew-vs-crew street dance face-off. Breaking, popping, and hip-hop.", event_date: "2026-11-15", event_time: "04:00 PM", venue: "Student Activity Quadrangle", capacity: 200, fee: 0, icon: "🕺", available_seats: 199 },
  { id: 5, title: "Inter-College Futsal", category: "Sports", description: "5-a-side football tournament. Fast-paced knockout matches on turf.", event_date: "2026-11-14", event_time: "09:00 AM", venue: "Sports Complex Turf", capacity: 16, fee: 200, icon: "⚽", available_seats: 15 },
  { id: 6, title: "Box Cricket Cup", category: "Sports", description: "Over-arm gully cricket under floodlights. 6 overs per innings, 7 players a side.", event_date: "2026-11-15", event_time: "10:00 AM", venue: "Main Grounds Box", capacity: 32, fee: 100, icon: "🏏", available_seats: 31 },
  { id: 7, title: "FIFA 24 Tournament", category: "Gaming", description: "Console esports tournament on PS5. Single-elimination bracket, tactical matches.", event_date: "2026-11-14", event_time: "11:00 AM", venue: "Gaming Lounge, Room 102", capacity: 64, fee: 50, icon: "🎮", available_seats: 62 },
  { id: 8, title: "Valorant Showdown", category: "Gaming", description: "5v5 competitive shooter tournament. Custom lobbies, standard competitive rules.", event_date: "2026-11-15", event_time: "01:00 PM", venue: "Computing Lab 1", capacity: 16, fee: 0, icon: "🎯", available_seats: 15 },
  { id: 9, title: "Intro to Vibe Coding", category: "Workshop", description: "Hands-on session: building full-stack web and mobile apps using AI coding assistants.", event_date: "2026-11-14", event_time: "11:30 AM", venue: "Seminar Hall A", capacity: 120, fee: 0, icon: "✨", available_seats: 119 },
  { id: 10, title: "UI/UX Masterclass", category: "Workshop", description: "From wireframes to design systems in Figma. Interactive case studies by product designers.", event_date: "2026-11-15", event_time: "02:30 PM", venue: "Design Studio, Block B", capacity: 80, fee: 0, icon: "🎨", available_seats: 79 },
  { id: 11, title: "Acoustic Night", category: "Cultural", description: "Unplugged vocal and instrumental performances under the stars. Open-mic segment included.", event_date: "2026-11-14", event_time: "08:30 PM", venue: "Amphitheatre Lawn", capacity: 300, fee: 0, icon: "🌙", available_seats: 299 },
  { id: 12, title: "AI Pitch Fest", category: "Technical", description: "Startup ideas powered by generative AI. Pitch to a panel of founders and engineers.", event_date: "2026-11-15", event_time: "11:00 AM", venue: "Auditorium", capacity: 40, fee: 0, icon: "💡", available_seats: 39 },
  { id: 13, title: "Web Design Rumble", category: "Technical", description: "Timed front-end styling challenge. Replicate given mocks with CSS and JavaScript.", event_date: "2026-11-15", event_time: "03:00 PM", venue: "Lab 2, Computing Block", capacity: 50, fee: 0, icon: "🌐", available_seats: 49 },
  { id: 14, title: "Street Play (Nukkad)", category: "Cultural", description: "Original street theatre performances on social issues. High energy, live drums.", event_date: "2026-11-15", event_time: "05:00 PM", venue: "Central Plaza", capacity: 150, fee: 0, icon: "🎭", available_seats: 149 },
  { id: 15, title: "Chess Championship", category: "Sports", description: "FIDE-rules rapid swiss tournament testing tactical brilliance and end-game strategy.", event_date: "2026-11-15", event_time: "09:00 AM", venue: "Indoor Games Hall", capacity: 32, fee: 0, icon: "♟️", available_seats: 32 }
];

function getLocalEvents() {
  const stored = localStorage.getItem("zenith26_demo_events");
  if (stored) {
    try { return JSON.parse(stored); } catch (e) {}
  }
  return JSON.parse(JSON.stringify(DEFAULT_FEST_EVENTS));
}

const DEFAULT_SAMPLE_REGS = [
  { id: 1, event_id: 1, student_name: "Aarav Sharma", roll_number: "2024BCSE042", email: "aarav.sharma@college.edu", phone: "+91 9876543210", registered_at: "2026-10-13 10:00:00", event_title: "Code Sprint 2.0" },
  { id: 2, event_id: 2, student_name: "Priya Patel", roll_number: "2024BIT019", email: "priya.p@college.edu", phone: "+91 9812345678", registered_at: "2026-10-13 10:30:00", event_title: "Robo Race" },
  { id: 3, event_id: 3, student_name: "Rohan Verma", roll_number: "2024BME088", email: "rohan.v@college.edu", phone: null, registered_at: "2026-10-13 11:00:00", event_title: "Battle of the Bands" }
];

// ==========================================
// Initialization
// ==========================================

async function loadDashboard() {
  await Promise.all([fetchStats(), fetchEventsTable(), fetchRegistrations()]);
}

// ==========================================
// 1. Fetch & Render Stats
// ==========================================

async function fetchStats() {
  try {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 2000);
    const res = await fetch("/api/stats", { signal: controller.signal });
    clearTimeout(timeoutId);

    if (!res.ok) throw new Error("Failed to load fest stats");
    const stats = await res.json();

    statTotalEvents.textContent = stats.total_events;
    statTotalRegistrations.textContent = stats.total_registrations;
    statUtilizationPct.textContent = `${stats.overall_utilization_pct}%`;
    statUtilizationBar.style.width = `${Math.min(100, stats.overall_utilization_pct)}%`;
    statUpcomingEvents.textContent = stats.upcoming_events_count;

    renderCategoryBreakdown(stats.events_by_category);
  } catch (err) {
    console.warn("Stats API unavailable, calculating from local dataset:", err);
    const events = getLocalEvents();
    const totalEvents = events.length;
    let totalCap = 0;
    let totalReg = 0;
    const catMap = {};
    events.forEach((e) => {
      totalCap += e.capacity;
      totalReg += (e.capacity - e.available_seats);
      catMap[e.category] = (catMap[e.category] || 0) + 1;
    });
    const utilPct = totalCap > 0 ? Math.round((totalReg / totalCap) * 1000) / 10 : 0;

    statTotalEvents.textContent = totalEvents;
    statTotalRegistrations.textContent = totalReg;
    statUtilizationPct.textContent = `${utilPct}%`;
    statUtilizationBar.style.width = `${Math.min(100, utilPct)}%`;
    statUpcomingEvents.textContent = totalEvents;

    renderCategoryBreakdown(catMap);
  }
}

function renderCategoryBreakdown(catDict) {
  categoryBreakdownContainer.innerHTML = "";
  if (!catDict || Object.keys(catDict).length === 0) {
    categoryBreakdownContainer.innerHTML = `<span class="text-xs text-slate-500">No categories loaded.</span>`;
    return;
  }

  for (const [catName, count] of Object.entries(catDict)) {
    const colorClass = catColorMap[catName] || "bg-slate-900 text-slate-300 border-slate-700";
    const box = document.createElement("div");
    box.className = `p-3.5 rounded-xl border ${colorClass} flex flex-col justify-between`;
    box.innerHTML = `
      <span class="text-[11px] font-bold uppercase tracking-wider">${escapeHtml(catName)}</span>
      <span class="text-xl font-black mt-1 text-white">${count} <span class="text-xs font-normal text-slate-400">events</span></span>
    `;
    categoryBreakdownContainer.appendChild(box);
  }
}

// ==========================================
// 2. Fetch & Render Events Table
// ==========================================

async function fetchEventsTable() {
  try {
    eventsTableBody.innerHTML = `
      <tr>
        <td colspan="7" class="py-8 text-center text-xs text-slate-400">
          <span class="inline-block animate-spin rounded-full h-4 w-4 border-2 border-cyan-400 border-t-transparent mr-2"></span>
          Loading events...
        </td>
      </tr>
    `;

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 2000);
    const res = await fetch("/api/events", { signal: controller.signal });
    clearTimeout(timeoutId);

    if (!res.ok) throw new Error("Failed to load events");
    const events = await res.json();
    allEvents = events;

    eventsTabBadge.textContent = events.length;
    updateEventDropdown(events);

    const processed = events.map((ev) => {
      const registered = ev.capacity - ev.available_seats;
      const utilPct = ev.capacity > 0 ? (registered / ev.capacity) * 100 : 0;
      return {
        ...ev,
        registered,
        utilPct: Math.round(utilPct * 10) / 10,
      };
    });

    processed.sort((a, b) => b.utilPct - a.utilPct);
    renderEventsTable(processed);
  } catch (err) {
    console.warn("Events table API unavailable, using local dataset:", err);
    const events = getLocalEvents();
    allEvents = events;

    eventsTabBadge.textContent = events.length;
    updateEventDropdown(events);

    const processed = events.map((ev) => {
      const registered = ev.capacity - ev.available_seats;
      const utilPct = ev.capacity > 0 ? (registered / ev.capacity) * 100 : 0;
      return {
        ...ev,
        registered,
        utilPct: Math.round(utilPct * 10) / 10,
      };
    });

    processed.sort((a, b) => b.utilPct - a.utilPct);
    renderEventsTable(processed);
  }
}

function renderEventsTable(events) {
  eventsTableBody.innerHTML = "";

  if (events.length === 0) {
    eventsTableBody.innerHTML = `
      <tr>
        <td colspan="7" class="py-6 text-center text-xs text-slate-400">No events found.</td>
      </tr>
    `;
    return;
  }

  events.forEach((ev) => {
    const isHighDemand = ev.utilPct >= 90;
    const isFull = ev.available_seats === 0;

    const tr = document.createElement("tr");
    tr.className = `hover:bg-slate-800/50 transition-colors ${
      isHighDemand ? "bg-amber-950/30 text-amber-200 border-l-2 border-amber-500" : ""
    }`;

    // Utilization badge & progress bar
    let utilBadge = "";
    if (isFull) {
      utilBadge = `<span class="px-2 py-0.5 rounded-full text-[11px] font-bold bg-rose-950/80 text-rose-300 border border-rose-500/40">100% (FULL)</span>`;
    } else if (isHighDemand) {
      utilBadge = `<span class="px-2 py-0.5 rounded-full text-[11px] font-bold bg-amber-950/80 text-amber-300 border border-amber-500/40 animate-pulse">⚠️ ${ev.utilPct}%</span>`;
    } else {
      utilBadge = `<span class="text-xs text-slate-300 font-semibold">${ev.utilPct}%</span>`;
    }

    const catBadge = catColorMap[ev.category] || "bg-slate-800 text-slate-300 border-slate-700";

    tr.innerHTML = `
      <td class="py-3.5 px-4">
        <div class="flex items-center gap-2.5">
          <span class="text-lg p-1 bg-slate-800 rounded-lg border border-slate-700">${ev.icon || "⚡"}</span>
          <div>
            <span class="font-bold text-white block leading-tight">${escapeHtml(ev.title)}</span>
            <span class="text-[11px] text-slate-500">ID: #${ev.id}</span>
          </div>
        </div>
      </td>
      <td class="py-3.5 px-4">
        <span class="px-2.5 py-0.5 rounded-md text-[11px] font-semibold uppercase tracking-wider border ${catBadge}">
          ${escapeHtml(ev.category)}
        </span>
      </td>
      <td class="py-3.5 px-4 text-xs text-slate-300">
        <div>${escapeHtml(ev.event_date)}</div>
        <div class="text-slate-500">${escapeHtml(ev.event_time)}</div>
      </td>
      <td class="py-3.5 px-4 text-xs text-slate-300">
        ${escapeHtml(ev.venue)}
      </td>
      <td class="py-3.5 px-4 text-xs font-semibold">
        <span class="${isFull ? 'text-rose-400' : 'text-slate-100'}">${ev.registered}</span>
        <span class="text-slate-500 font-normal"> / ${ev.capacity}</span>
      </td>
      <td class="py-3.5 px-4">
        <div class="flex items-center gap-2">
          ${utilBadge}
          <div class="w-16 bg-slate-800 rounded-full h-1.5 overflow-hidden hidden sm:block">
            <div class="${isHighDemand ? 'bg-amber-400' : 'bg-cyan-400'} h-1.5 rounded-full" style="width: ${Math.min(100, ev.utilPct)}%"></div>
          </div>
        </div>
      </td>
      <td class="py-3.5 px-4 text-right font-semibold text-xs">
        ${ev.fee === 0 ? '<span class="text-emerald-400">Free</span>' : `<span class="text-cyan-300">₹${ev.fee}</span>`}
      </td>
    `;
    eventsTableBody.appendChild(tr);
  });
}

function updateEventDropdown(events) {
  const currentVal = filterRegEvent.value;
  filterRegEvent.innerHTML = `<option value="">All Events (${events.length})</option>`;
  events.forEach((ev) => {
    const opt = document.createElement("option");
    opt.value = ev.id;
    opt.textContent = `${ev.icon || "⚡"} ${ev.title}`;
    if (String(ev.id) === String(currentVal)) {
      opt.selected = true;
    }
    filterRegEvent.appendChild(opt);
  });
}

// ==========================================
// 3. Fetch & Render Raw Registrations Table
// ==========================================

async function fetchRegistrations(eventIdFilter = "") {
  try {
    registrationsTableBody.innerHTML = `
      <tr>
        <td colspan="7" class="py-8 text-center text-xs text-slate-400">
          <span class="inline-block animate-spin rounded-full h-4 w-4 border-2 border-cyan-400 border-t-transparent mr-2"></span>
          Loading registrations...
        </td>
      </tr>
    `;

    let url = "/api/registrations";
    if (eventIdFilter) {
      url += `?event_id=${eventIdFilter}`;
    }

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 2000);
    const res = await fetch(url, { signal: controller.signal });
    clearTimeout(timeoutId);

    if (!res.ok) throw new Error("Failed to load registrations");
    const regs = await res.json();
    allRegistrations = regs;

    if (!eventIdFilter) {
      regTabBadge.textContent = regs.length;
    }
    regCountInfo.textContent = `Showing ${regs.length} student registration${regs.length === 1 ? "" : "s"}`;

    renderRegistrationsTable(regs);
  } catch (err) {
    console.warn("Registrations API unavailable, using sample dataset:", err);
    let regs = DEFAULT_SAMPLE_REGS;
    if (eventIdFilter) {
      regs = regs.filter((r) => r.event_id === parseInt(eventIdFilter, 10));
    }
    allRegistrations = regs;

    if (!eventIdFilter) {
      regTabBadge.textContent = regs.length;
    }
    regCountInfo.textContent = `Showing ${regs.length} student registration${regs.length === 1 ? "" : "s"}`;
    renderRegistrationsTable(regs);
  }
}

function renderRegistrationsTable(regs) {
  registrationsTableBody.innerHTML = "";

  if (regs.length === 0) {
    registrationsTableBody.innerHTML = `
      <tr>
        <td colspan="7" class="py-8 text-center text-xs text-slate-400">
          No registrations found for this filter.
        </td>
      </tr>
    `;
    return;
  }

  regs.forEach((r) => {
    const tr = document.createElement("tr");
    tr.className = "hover:bg-slate-800/50 transition-colors";

    const formattedTime = formatTimestamp(r.registered_at);

    tr.innerHTML = `
      <td class="py-3 px-4 font-mono text-xs text-slate-500">#${r.id}</td>
      <td class="py-3 px-4 font-bold text-white">${escapeHtml(r.student_name)}</td>
      <td class="py-3 px-4 font-mono text-xs text-slate-400">${escapeHtml(r.roll_number)}</td>
      <td class="py-3 px-4 text-xs text-slate-300">
        <div>${escapeHtml(r.email)}</div>
        ${r.phone ? `<div class="text-slate-500 text-[11px]">${escapeHtml(r.phone)}</div>` : ""}
      </td>
      <td class="py-3 px-4 text-xs font-semibold text-cyan-300">${escapeHtml(r.event_title)}</td>
      <td class="py-3 px-4 text-xs text-slate-400">${formattedTime}</td>
      <td class="py-3 px-4 text-center">
        <!-- Stretch Goal 2: Cancel Registration Action -->
        <button 
          data-reg-id="${r.id}" 
          data-student-name="${escapeHtml(r.student_name)}" 
          data-event-title="${escapeHtml(r.event_title)}"
          class="cancel-reg-btn text-rose-400 hover:text-white hover:bg-rose-900/60 px-2.5 py-1 rounded-lg text-xs font-semibold border border-rose-500/30 transition-all"
          title="Cancel this registration"
        >
          Cancel
        </button>
      </td>
    `;
    registrationsTableBody.appendChild(tr);
  });

  // Attach cancel listeners
  const cancelBtns = registrationsTableBody.querySelectorAll(".cancel-reg-btn");
  cancelBtns.forEach((btn) => {
    btn.addEventListener("click", () => {
      const regId = btn.getAttribute("data-reg-id");
      const name = btn.getAttribute("data-student-name");
      const eventTitle = btn.getAttribute("data-event-title");
      handleCancelRegistration(regId, name, eventTitle);
    });
  });
}

// ==========================================
// Stretch Goal 2: Cancel Registration Handler
// ==========================================

async function handleCancelRegistration(regId, studentName, eventTitle) {
  const confirmed = confirm(
    `Are you sure you want to cancel the registration for "${studentName}" in "${eventTitle}"? This will free up 1 seat immediately.`
  );
  if (!confirmed) return;

  try {
    const res = await fetch(`/api/registrations/${regId}`, {
      method: "DELETE",
    });

    if (res.ok) {
      alert(`Registration #${regId} cancelled successfully.`);
      // Refresh stats, tables, and dropdowns
      await loadDashboard();
    } else {
      const err = await res.json();
      alert(`Failed to cancel registration: ${err.detail || res.statusText}`);
    }
  } catch (err) {
    alert(`Network error: ${err.message}`);
  }
}

// ==========================================
// Stretch Goal 1: Add New Event Modal & Form
// ==========================================

openAddEventModalBtn.addEventListener("click", () => {
  addEventAlert.classList.add("hidden");
  addEventForm.reset();
  addEventModal.classList.remove("hidden");
});

closeAddEventModalBtn.addEventListener("click", () => {
  addEventModal.classList.add("hidden");
});

addEventModal.addEventListener("click", (e) => {
  if (e.target === addEventModal) {
    addEventModal.classList.add("hidden");
  }
});

addEventForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  const payload = {
    title: document.getElementById("add-title").value.trim(),
    category: document.getElementById("add-category").value.trim(),
    icon: document.getElementById("add-icon").value.trim() || "🎉",
    event_date: document.getElementById("add-date").value.trim(),
    event_time: document.getElementById("add-time").value.trim(),
    venue: document.getElementById("add-venue").value.trim(),
    capacity: parseInt(document.getElementById("add-capacity").value, 10),
    fee: parseInt(document.getElementById("add-fee").value, 10) || 0,
    description: document.getElementById("add-description").value.trim() || "",
  };

  submitAddEventBtn.disabled = true;
  submitAddEventBtn.textContent = "Saving...";

  try {
    const res = await fetch("/api/events", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    const data = await res.json();

    if (res.status === 201) {
      addEventAlert.className = "p-3 rounded-xl font-medium bg-emerald-950/80 text-emerald-300 border border-emerald-500/40 block text-xs";
      addEventAlert.textContent = `Event "${data.title}" created successfully!`;

      await loadDashboard();

      setTimeout(() => {
        addEventModal.classList.add("hidden");
        addEventAlert.classList.add("hidden");
      }, 1200);
    } else {
      addEventAlert.className = "p-3 rounded-xl font-medium bg-rose-950/80 text-rose-300 border border-rose-500/40 block text-xs";
      addEventAlert.textContent = data.detail || "Error creating event.";
    }
  } catch (err) {
    addEventAlert.className = "p-3 rounded-xl font-medium bg-rose-950/80 text-rose-300 border border-rose-500/40 block text-xs";
    addEventAlert.textContent = "Network error creating event.";
  } finally {
    submitAddEventBtn.disabled = false;
    submitAddEventBtn.textContent = "Save Event";
  }
});

// ==========================================
// Tabs & Event Listeners
// ==========================================

tabEventsBtn.addEventListener("click", () => {
  tabEventsBtn.className = "tab-btn pb-3 text-sm font-bold border-b-2 border-cyan-400 text-cyan-300 transition-colors flex items-center gap-2";
  tabRegistrationsBtn.className = "tab-btn pb-3 text-sm font-bold border-b-2 border-transparent text-slate-400 hover:text-slate-200 transition-colors flex items-center gap-2";
  sectionEventsTable.classList.remove("hidden");
  sectionRegistrationsTable.classList.add("hidden");
});

tabRegistrationsBtn.addEventListener("click", () => {
  tabRegistrationsBtn.className = "tab-btn pb-3 text-sm font-bold border-b-2 border-cyan-400 text-cyan-300 transition-colors flex items-center gap-2";
  tabEventsBtn.className = "tab-btn pb-3 text-sm font-bold border-b-2 border-transparent text-slate-400 hover:text-slate-200 transition-colors flex items-center gap-2";
  sectionEventsTable.classList.add("hidden");
  sectionRegistrationsTable.classList.remove("hidden");
  fetchRegistrations(filterRegEvent.value);
});

filterRegEvent.addEventListener("change", (e) => {
  fetchRegistrations(e.target.value);
});

refreshDashboardBtn.addEventListener("click", async () => {
  refreshDashboardBtn.classList.add("animate-spin");
  await loadDashboard();
  setTimeout(() => {
    refreshDashboardBtn.classList.remove("animate-spin");
  }, 400);
});

// ==========================================
// Helpers
// ==========================================

function formatTimestamp(isoStr) {
  if (!isoStr) return "";
  try {
    const d = new Date(isoStr);
    return d.toLocaleString("en-US", {
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch {
    return isoStr;
  }
}

function escapeHtml(str) {
  if (!str) return "";
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

// Initial Load
document.addEventListener("DOMContentLoaded", () => {
  loadDashboard();
});

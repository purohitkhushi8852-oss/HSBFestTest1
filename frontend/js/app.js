// Zenith '26 — Student Event Portal Client JavaScript

let cachedEvents = [];
let currentCategory = "";
let currentSearch = "";
let searchDebounceTimer = null;

// Category styling map matching admin dashboard & Android app
const catColorMap = {
  Technical: {
    badge: "bg-cyan-950/80 text-cyan-300 border-cyan-500/30",
    border: "border-cyan-500/20",
    bar: "bg-cyan-400"
  },
  Cultural: {
    badge: "bg-fuchsia-950/80 text-fuchsia-300 border-fuchsia-500/30",
    border: "border-fuchsia-500/20",
    bar: "bg-fuchsia-400"
  },
  Sports: {
    badge: "bg-emerald-950/80 text-emerald-300 border-emerald-500/30",
    border: "border-emerald-500/20",
    bar: "bg-emerald-400"
  },
  Gaming: {
    badge: "bg-amber-950/80 text-amber-300 border-amber-500/30",
    border: "border-amber-500/20",
    bar: "bg-amber-400"
  },
  Workshop: {
    badge: "bg-violet-950/80 text-violet-300 border-violet-500/30",
    border: "border-violet-500/20",
    bar: "bg-violet-400"
  }
};

const defaultColor = {
  badge: "bg-slate-800 text-slate-300 border-slate-700",
  border: "border-slate-800",
  bar: "bg-cyan-400"
};

// Fallback festival events dataset (matching events_seed.xlsx & Android LocalFestDataSource)
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

function saveLocalEvents(events) {
  localStorage.setItem("zenith26_demo_events", JSON.stringify(events));
}

// DOM References
const eventsGrid = document.getElementById("events-grid");
const loadingState = document.getElementById("loading-state");
const errorState = document.getElementById("error-state");
const errorMessage = document.getElementById("error-message");
const emptyState = document.getElementById("empty-state");
const retryBtn = document.getElementById("retry-btn");
const clearFiltersBtn = document.getElementById("clear-filters-btn");
const searchInput = document.getElementById("search-input");
const categoryFilters = document.getElementById("category-filters");
const eventsCountBadge = document.getElementById("events-count-badge");
const filterActiveIndicator = document.getElementById("filter-active-indicator");

// Modal Elements
const detailModal = document.getElementById("detail-modal");
const modalCloseBtn = document.getElementById("modal-close-btn");
const modalEventIcon = document.getElementById("modal-event-icon");
const modalEventCategory = document.getElementById("modal-event-category");
const modalEventSeatsBadge = document.getElementById("modal-event-seats-badge");
const modalEventTitle = document.getElementById("modal-event-title");
const modalEventDesc = document.getElementById("modal-event-desc");
const modalEventDate = document.getElementById("modal-event-date");
const modalEventTime = document.getElementById("modal-event-time");
const modalEventVenue = document.getElementById("modal-event-venue");
const modalEventFee = document.getElementById("modal-event-fee");
const modalAlert = document.getElementById("modal-alert");

// Form Elements
const registrationForm = document.getElementById("registration-form");
const regEventId = document.getElementById("reg-event-id");
const regName = document.getElementById("reg-name");
const regRoll = document.getElementById("reg-roll");
const regEmail = document.getElementById("reg-email");
const regPhone = document.getElementById("reg-phone");
const regSubmitBtn = document.getElementById("reg-submit-btn");
const submitBtnSpinner = document.getElementById("submit-btn-spinner");
const submitBtnText = document.getElementById("submit-btn-text");

// ==========================================
// Initialization & Event Fetching
// ==========================================

document.addEventListener("DOMContentLoaded", () => {
  // Instant render fallback events so there is zero delay or blank state
  try {
    const initial = getLocalEvents();
    cachedEvents = initial;
    renderEvents(initial);
  } catch (e) {
    console.warn("Initial render error:", e);
  }

  fetchEvents();
  setupEventListeners();
});

function setupEventListeners() {
  // Search debouncing
  if (searchInput) {
    searchInput.addEventListener("input", (e) => {
      clearTimeout(searchDebounceTimer);
      searchDebounceTimer = setTimeout(() => {
        currentSearch = e.target.value.trim();
        fetchEvents();
      }, 300);
    });
  }

  // Category filter pills
  if (categoryFilters) {
    categoryFilters.addEventListener("click", (e) => {
      const btn = e.target.closest(".category-btn");
      if (!btn) return;

      document.querySelectorAll(".category-btn").forEach((b) => {
        b.className = "category-btn px-4 py-1.5 rounded-full text-xs font-semibold bg-slate-900/80 text-slate-300 border border-slate-700/60 hover:text-cyan-300 hover:border-cyan-500/40 transition-all";
      });

      btn.className = "category-btn active px-4 py-1.5 rounded-full text-xs font-bold transition-all bg-gradient-to-r from-cyan-500 to-violet-600 text-white shadow-md shadow-violet-950/30";
      currentCategory = btn.getAttribute("data-category") || "";
      fetchEvents();
    });
  }

  // Retry & Reset
  if (retryBtn) retryBtn.addEventListener("click", fetchEvents);
  if (clearFiltersBtn) {
    clearFiltersBtn.addEventListener("click", () => {
      currentCategory = "";
      currentSearch = "";
      if (searchInput) searchInput.value = "";
      document.querySelectorAll(".category-btn").forEach((b, idx) => {
        if (idx === 0) {
          b.className = "category-btn active px-4 py-1.5 rounded-full text-xs font-bold transition-all bg-gradient-to-r from-cyan-500 to-violet-600 text-white shadow-md shadow-violet-950/30";
        } else {
          b.className = "category-btn px-4 py-1.5 rounded-full text-xs font-semibold bg-slate-900/80 text-slate-300 border border-slate-700/60 hover:text-cyan-300 hover:border-cyan-500/40 transition-all";
        }
      });
      fetchEvents();
    });
  }

  // Modal close handlers
  if (modalCloseBtn) modalCloseBtn.addEventListener("click", closeModal);
  if (detailModal) {
    detailModal.addEventListener("click", (e) => {
      if (e.target === detailModal) closeModal();
    });
  }
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && detailModal && !detailModal.classList.contains("hidden")) {
      closeModal();
    }
  });

  // Registration Form Submission
  if (registrationForm) {
    registrationForm.addEventListener("submit", handleRegistrationSubmit);
  }
}

async function fetchEvents() {
  showLoading();

  try {
    const params = new URLSearchParams();
    if (currentCategory && currentCategory !== "All") params.append("category", currentCategory);
    if (currentSearch) params.append("search", currentSearch);

    const url = `/api/events${params.toString() ? "?" + params.toString() : ""}`;
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 2500);

    const res = await fetch(url, { signal: controller.signal });
    clearTimeout(timeoutId);

    if (!res.ok) {
      throw new Error(`HTTP Error ${res.status}`);
    }

    const events = await res.json();
    cachedEvents = events;
    renderEvents(events);
  } catch (err) {
    console.warn("API request failed, activating interactive fallback:", err);
    let events = getLocalEvents();

    if (currentCategory && currentCategory !== "All") {
      events = events.filter((e) => e.category.toLowerCase() === currentCategory.toLowerCase());
    }

    if (currentSearch) {
      const q = currentSearch.toLowerCase();
      events = events.filter((e) =>
        (e.title && e.title.toLowerCase().includes(q)) ||
        (e.description && e.description.toLowerCase().includes(q))
      );
    }

    cachedEvents = events;
    renderEvents(events);
  }
}

function renderEvents(events) {
  hideLoading();

  if (!events || events.length === 0) {
    eventsGrid.innerHTML = "";
    emptyState.classList.remove("hidden");
    eventsCountBadge.textContent = "0 Events";
    if (currentCategory || currentSearch) {
      filterActiveIndicator.classList.remove("hidden");
    } else {
      filterActiveIndicator.classList.add("hidden");
    }
    return;
  }

  emptyState.classList.add("hidden");
  errorState.classList.add("hidden");
  eventsCountBadge.textContent = `${events.length} Event${events.length === 1 ? "" : "s"}`;
  
  if (currentCategory || currentSearch) {
    filterActiveIndicator.classList.remove("hidden");
  } else {
    filterActiveIndicator.classList.add("hidden");
  }

  eventsGrid.innerHTML = events.map((ev) => createEventCardHtml(ev)).join("");

  // Attach click listeners to cards
  document.querySelectorAll(".view-details-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      const eventId = parseInt(btn.getAttribute("data-event-id"), 10);
      const ev = cachedEvents.find((e) => e.id === eventId);
      if (ev) openEventModal(ev);
    });
  });
}

function createEventCardHtml(ev) {
  const catColor = catColorMap[ev.category] || defaultColor;
  const isFull = ev.available_seats <= 0;
  const seatsPct = Math.min(100, Math.round(((ev.capacity - ev.available_seats) / ev.capacity) * 100));

  const feeBadge = ev.fee === 0
    ? `<span class="px-2 py-0.5 rounded-md text-[11px] font-bold bg-emerald-950/80 text-emerald-400 border border-emerald-500/30">Free</span>`
    : `<span class="px-2 py-0.5 rounded-md text-[11px] font-bold bg-slate-800 text-amber-300 border border-slate-700">₹${ev.fee}</span>`;

  const seatsBadge = isFull
    ? `<span class="px-2 py-0.5 rounded-md text-[11px] font-black bg-rose-950/90 text-rose-300 border border-rose-500/40">FULL</span>`
    : `<span class="text-xs font-semibold ${ev.available_seats < 15 ? 'text-amber-400' : 'text-slate-300'}">${ev.available_seats} / ${ev.capacity} left</span>`;

  return `
    <div class="card-hover bg-[#0f172a] border border-slate-800/90 hover:border-slate-700 rounded-2xl p-5 flex flex-col justify-between shadow-lg relative overflow-hidden group">
      <!-- Glow hint -->
      <div class="absolute -top-12 -right-12 w-28 h-28 bg-violet-600/5 rounded-full blur-2xl group-hover:bg-violet-600/15 transition-all"></div>

      <div>
        <!-- Top bar of card -->
        <div class="flex items-start justify-between gap-3 mb-3">
          <div class="w-12 h-12 rounded-xl bg-slate-800/90 border border-slate-700/80 flex items-center justify-center text-2xl flex-shrink-0 group-hover:scale-105 transition-transform">
            ${ev.icon || '🎉'}
          </div>
          <div class="flex flex-col items-end gap-1.5">
            <span class="px-2.5 py-0.5 rounded-full text-[11px] font-bold border ${catColor.badge}">
              ${ev.category}
            </span>
            ${feeBadge}
          </div>
        </div>

        <!-- Event Title -->
        <h3 class="text-lg font-bold text-white group-hover:text-cyan-300 transition-colors line-clamp-1 mb-2">
          ${escapeHtml(ev.title)}
        </h3>

        <!-- Description Snippet -->
        <p class="text-xs text-slate-400 line-clamp-2 leading-relaxed mb-4">
          ${escapeHtml(ev.description || "Join this exciting fest event at Zenith '26.")}
        </p>
      </div>

      <!-- Footer Section -->
      <div class="space-y-3.5 pt-3 border-t border-slate-800/80">
        <!-- Date, Time, Venue -->
        <div class="space-y-1 text-xs text-slate-400">
          <div class="flex items-center gap-2">
            <span>📅</span>
            <span class="text-slate-300">${formatDate(ev.event_date)} • ${ev.event_time}</span>
          </div>
          <div class="flex items-center gap-2">
            <span>📍</span>
            <span class="text-slate-300 line-clamp-1">${escapeHtml(ev.venue)}</span>
          </div>
        </div>

        <!-- Capacity Progress Bar -->
        <div>
          <div class="flex items-center justify-between text-xs mb-1">
            <span class="text-slate-400 text-[11px]">Seat Capacity</span>
            ${seatsBadge}
          </div>
          <div class="w-full h-1.5 bg-slate-800 rounded-full overflow-hidden">
            <div 
              class="h-full ${isFull ? 'bg-rose-500' : catColor.bar} transition-all duration-500 rounded-full" 
              style="width: ${seatsPct}%"
            ></div>
          </div>
        </div>

        <!-- Action Button -->
        <button 
          data-event-id="${ev.id}"
          class="view-details-btn w-full py-2.5 px-4 rounded-xl text-xs font-bold transition-all flex items-center justify-center gap-1.5 ${
            isFull 
              ? 'bg-slate-800 text-slate-400 hover:bg-slate-750 border border-slate-700/60' 
              : 'bg-gradient-to-r from-cyan-500 to-violet-600 hover:from-cyan-400 hover:to-violet-500 text-white shadow-md shadow-violet-950/30'
          }"
        >
          <span>${isFull ? 'View Details (Full)' : 'View Details & Register'}</span>
          <span>→</span>
        </button>
      </div>
    </div>
  `;
}

// ==========================================
// Modal & Registration Logic
// ==========================================

function openEventModal(ev) {
  regEventId.value = ev.id;
  modalEventTitle.textContent = ev.title;
  modalEventDesc.textContent = ev.description || "No full description provided.";
  modalEventIcon.textContent = ev.icon || "🎉";
  modalEventDate.textContent = formatDate(ev.event_date);
  modalEventTime.textContent = ev.event_time;
  modalEventVenue.textContent = ev.venue;
  modalEventFee.textContent = ev.fee === 0 ? "Free" : `₹${ev.fee}`;

  const catColor = catColorMap[ev.category] || defaultColor;
  modalEventCategory.className = `px-2.5 py-0.5 rounded-full text-[11px] font-bold border ${catColor.badge}`;
  modalEventCategory.textContent = ev.category;

  const isFull = ev.available_seats <= 0;
  if (isFull) {
    modalEventSeatsBadge.className = "px-2.5 py-0.5 rounded-full text-[11px] font-black bg-rose-950 text-rose-300 border border-rose-500/40";
    modalEventSeatsBadge.textContent = "FULL (0 Seats Left)";
    regSubmitBtn.disabled = true;
    regSubmitBtn.className = "w-full py-3 px-4 rounded-xl text-xs sm:text-sm font-bold bg-slate-800 text-slate-500 cursor-not-allowed border border-slate-700";
    submitBtnText.textContent = "Event is Full";
  } else {
    modalEventSeatsBadge.className = "px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-emerald-950 text-emerald-300 border border-emerald-500/30";
    modalEventSeatsBadge.textContent = `${ev.available_seats} Seats Left`;
    regSubmitBtn.disabled = false;
    regSubmitBtn.className = "w-full py-3 px-4 rounded-xl text-xs sm:text-sm font-bold text-white bg-gradient-to-r from-cyan-500 via-violet-600 to-fuchsia-600 hover:from-cyan-400 hover:to-violet-500 transition-all shadow-lg shadow-violet-950/40 flex items-center justify-center gap-2";
    submitBtnText.textContent = "Confirm Registration";
  }

  // Reset form & errors
  hideModalAlert();
  document.querySelectorAll(".field-error").forEach((el) => el.classList.add("hidden"));
  registrationForm.reset();

  detailModal.classList.remove("hidden");
  document.body.style.overflow = "hidden";
}

function closeModal() {
  if (detailModal) detailModal.classList.add("hidden");
  document.body.style.overflow = "";
}

async function handleRegistrationSubmit(e) {
  e.preventDefault();
  hideModalAlert();

  const eventId = parseInt(regEventId.value, 10);
  const nameVal = regName.value.trim();
  const rollVal = regRoll.value.trim();
  const emailVal = regEmail.value.trim();
  const phoneVal = regPhone.value.trim();

  // Basic client-side validation
  let hasError = false;
  if (!nameVal) {
    regName.nextElementSibling.classList.remove("hidden");
    hasError = true;
  } else {
    regName.nextElementSibling.classList.add("hidden");
  }

  if (!rollVal) {
    regRoll.nextElementSibling.classList.remove("hidden");
    hasError = true;
  } else {
    regRoll.nextElementSibling.classList.add("hidden");
  }

  if (!emailVal || !emailVal.includes("@") || !emailVal.includes(".")) {
    regEmail.nextElementSibling.classList.remove("hidden");
    hasError = true;
  } else {
    regEmail.nextElementSibling.classList.add("hidden");
  }

  if (hasError) return;

  // Set loading state on submit button
  regSubmitBtn.disabled = true;
  submitBtnSpinner.classList.remove("hidden");
  submitBtnText.textContent = "Submitting...";

  try {
    const res = await fetch(`/api/events/${eventId}/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        student_name: nameVal,
        roll_number: rollVal,
        email: emailVal,
        phone: phoneVal || null
      })
    });

    const data = await res.json().catch(() => ({}));

    if (res.status === 201) {
      // Success! Show confirmation
      showModalAlert(
        `🎉 Registered successfully for ${escapeHtml(modalEventTitle.textContent)}! Your seat is confirmed.`,
        "success"
      );
      registrationForm.reset();
      
      // Re-fetch events to update live seats immediately
      await fetchEvents();

      // Close modal after 1.8 seconds
      setTimeout(() => {
        closeModal();
      }, 1800);
    } else {
      // Show exact error message sent from server (400 or 409)
      const errMessage = data.detail || `Registration failed with status ${res.status}`;
      showModalAlert(`⚠️ ${errMessage}`, "error");
    }
  } catch (err) {
    console.warn("API registration failed, performing interactive demo registration:", err);
    // Offline / GitHub Pages Demo Registration
    const localEvents = getLocalEvents();
    const evIndex = localEvents.findIndex((e) => e.id === eventId);
    if (evIndex !== -1) {
      if (localEvents[evIndex].available_seats <= 0) {
        showModalAlert("⚠️ Event is full", "error");
        return;
      }
      localEvents[evIndex].available_seats = Math.max(0, localEvents[evIndex].available_seats - 1);
      saveLocalEvents(localEvents);

      showModalAlert(
        `🎉 Registered successfully for ${escapeHtml(modalEventTitle.textContent)}! Your seat is confirmed.`,
        "success"
      );
      registrationForm.reset();
      await fetchEvents();
      setTimeout(() => {
        closeModal();
      }, 1800);
      return;
    }
    showModalAlert("⚠️ Network error. Please check your connection to the server.", "error");
  } finally {
    regSubmitBtn.disabled = false;
    submitBtnSpinner.classList.add("hidden");
    submitBtnText.textContent = "Confirm Registration";
  }
}

function showModalAlert(msg, type) {
  modalAlert.textContent = msg;
  if (type === "success") {
    modalAlert.className = "mb-4 p-3 rounded-xl text-xs font-semibold bg-emerald-950/80 text-emerald-300 border border-emerald-500/40 block";
  } else {
    modalAlert.className = "mb-4 p-3 rounded-xl text-xs font-semibold bg-rose-950/80 text-rose-300 border border-rose-500/40 block";
  }
}

function hideModalAlert() {
  modalAlert.className = "hidden mb-4 p-3 rounded-xl text-xs font-semibold";
  modalAlert.textContent = "";
}

// ==========================================
// UI Helpers
// ==========================================

function showLoading() {
  loadingState.classList.remove("hidden");
  errorState.classList.add("hidden");
  emptyState.classList.add("hidden");
  eventsGrid.innerHTML = "";
}

function hideLoading() {
  loadingState.classList.add("hidden");
}

function showError(msg) {
  hideLoading();
  errorMessage.textContent = msg;
  errorState.classList.remove("hidden");
  eventsGrid.innerHTML = "";
}

function formatDate(dateStr) {
  if (!dateStr) return "";
  try {
    const parts = dateStr.split("-");
    if (parts.length === 3) {
      const months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
      const m = parseInt(parts[1], 10) - 1;
      return `${months[m]} ${parseInt(parts[2], 10)}, ${parts[0]}`;
    }
    return dateStr;
  } catch {
    return dateStr;
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

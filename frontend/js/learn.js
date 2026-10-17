// ==========================================================================
// Learn Early - Complete Frontend LMS Controller
// ==========================================================================

let currentNotePath = '';
let currentTopicTitle = '';
let dashboardData = null;
let activeStudyTool = 'anti-gravity';
let currentStudyMode = 'mcqs';
let currentTheme = 'dark';
let activeQuestions = [];
let quizScore = 0;
let answeredQuestions = new Set();

// Initialize Mermaid.js
if (window.mermaid) {
  mermaid.initialize({
    startOnLoad: false,
    theme: 'dark',
    themeVariables: {
      darkMode: true,
      background: '#090d16',
      primaryColor: '#14b8a6',
      primaryTextColor: '#f8fafc',
      primaryBorderColor: '#0d9488',
      lineColor: '#38bdf8',
      secondaryColor: '#8b5cf6',
      tertiaryColor: '#1e293b'
    }
  });
}

// Utility: JSON fetcher
async function fetchJson(url, options = {}) {
  const resp = await fetch(url, options);
  if (!resp.ok) {
    const err = await resp.text();
    throw new Error(`Request failed ${resp.status}: ${err}`);
  }
  return resp.json();
}

// ==========================================================================
// 1. Dashboard Initialization
// ==========================================================================

async function initDashboard() {
  try {
    dashboardData = await fetchJson('/api/lms/dashboard');
    applyUserSettings(dashboardData.user);
    renderToolkit(dashboardData.toolkit);
    renderLiveClass(dashboardData.live_class);
    renderKnowledgeGraphPreview(dashboardData.graph);
    renderChallenges(dashboardData.challenges);
  } catch (e) {
    console.error('Failed to load dashboard data:', e);
  }
}

function applyUserSettings(user) {
  if (!user) return;
  const nameEl = document.getElementById('header-user-name');
  const avatarEl = document.getElementById('header-avatar');
  const streakBar = document.getElementById('streak-bar');
  const streakVal = document.getElementById('streak-val');

  if (nameEl) nameEl.textContent = user.name || 'Khushi';
  if (avatarEl && user.avatar) avatarEl.src = user.avatar;
  if (streakBar) streakBar.style.width = `${user.streak_pct}%`;
  if (streakVal) streakVal.textContent = `${user.streak_pct}%`;

  if (user.theme) {
    setTheme(user.theme, false);
  }
}

function renderLiveClass(liveClass) {
  if (!liveClass) return;
  const dayEl = document.getElementById('live-class-day');
  const titleEl = document.getElementById('live-class-title');
  const subEl = document.getElementById('live-class-subtitle');
  const notesBtn = document.getElementById('live-class-notes-btn');

  if (dayEl) dayEl.textContent = liveClass.day;
  if (titleEl) titleEl.textContent = liveClass.title;
  if (subEl) subEl.textContent = liveClass.subtitle;
  if (notesBtn && liveClass.note_path) {
    notesBtn.onclick = () => openNoteByPath(encodeURIComponent(liveClass.note_path), liveClass.title);
  }
}

// Render Toolkit cards (Anti-Gravity, Postman, Obsidian, Pomeli AI, Claud Design, SQLite...)
function renderToolkit(tools) {
  const container = document.getElementById('toolkit-grid');
  if (!container || !tools) return;
  container.innerHTML = '';

  tools.forEach((tool) => {
    const card = document.createElement('div');
    card.className = 'tool-item-card flex flex-col justify-between';

    // Tool Icon representation
    let iconHtml = '';
    if (tool.icon === 'delta') {
      iconHtml = `<div class="w-7 h-7 rounded-lg bg-cyan-950/80 border border-cyan-500/50 flex items-center justify-center text-cyan-400 font-bold text-xs shadow-md">▲</div>`;
    } else if (tool.icon === 'rocket') {
      iconHtml = `<div class="w-7 h-7 rounded-lg bg-orange-950/80 border border-orange-500/50 flex items-center justify-center text-orange-400 text-xs shadow-md">🚀</div>`;
    } else if (tool.icon === 'gem') {
      iconHtml = `<div class="w-7 h-7 rounded-lg bg-purple-950/80 border border-purple-500/50 flex items-center justify-center text-purple-400 text-xs shadow-md">💎</div>`;
    } else if (tool.icon === 'f-logo') {
      iconHtml = `<div class="w-7 h-7 rounded-lg bg-purple-950/80 border border-purple-500/50 flex items-center justify-center text-purple-400 font-bold text-xs shadow-md">P</div>`;
    } else if (tool.icon === 'cloud') {
      iconHtml = `<div class="w-7 h-7 rounded-lg bg-blue-950/80 border border-blue-500/50 flex items-center justify-center text-blue-400 text-xs shadow-md">☁</div>`;
    } else {
      iconHtml = `<div class="w-7 h-7 rounded-lg bg-blue-950/80 border border-blue-500/50 flex items-center justify-center text-blue-400 text-xs shadow-md">🐬</div>`;
    }

    const topicsCount = tool.topics ? tool.topics.length : 7;

    let actionBlock = '';
    if (tool.completed_pct > 0) {
      actionBlock = `
        <div class="my-2">
          <div class="w-full bg-[#1e293b] h-1.5 rounded-full overflow-hidden mb-1">
            <div class="bg-gradient-to-r from-teal-500 to-cyan-400 h-full" style="width: ${tool.completed_pct}%"></div>
          </div>
          <div class="flex items-center justify-between text-[10px]">
            <span class="text-teal-400 font-medium">${tool.completed_pct}% Completed</span>
            <button onclick="openToolRoadmap('${tool.id}')" class="text-gray-400 hover:text-white underline">Topics (1-${topicsCount}) &gt;</button>
          </div>
        </div>`;
    } else {
      actionBlock = `
        <div class="my-2 flex items-center space-x-1.5">
          <button 
            onclick="openSandboxModal('${tool.name}')" 
            class="flex-1 bg-[#182335] hover:bg-teal-600 hover:text-white border border-white/10 text-gray-200 text-[11px] font-semibold py-1 px-2 rounded-lg transition text-center shadow-sm cursor-pointer">
            Start Sandbox
          </button>
          <button 
            onclick="openToolRoadmap('${tool.id}')" 
            class="bg-slate-800/80 hover:bg-slate-700 text-teal-400 hover:text-teal-300 text-[10px] font-semibold py-1 px-2 rounded-lg border border-teal-500/20 cursor-pointer" title="View 1, 2, 3... topics">
            Roadmap
          </button>
        </div>`;
    }

    card.innerHTML = `
      <div>
        <div class="flex items-start justify-between">
          <div class="flex items-center space-x-2">
            ${iconHtml}
            <div>
              <div class="text-xs font-bold text-white hover:text-teal-400 cursor-pointer" onclick="openToolRoadmap('${tool.id}')">
                ${tool.name}
              </div>
              <div class="text-[10px] text-gray-400">(${tool.category})</div>
            </div>
          </div>
          <button onclick="openToolRoadmap('${tool.id}')" class="text-gray-500 hover:text-teal-400 text-xs" title="Explore line-wise topics">⋮</button>
        </div>
        ${actionBlock}
      </div>

      <div class="pt-2 border-t border-white/[0.06] space-y-1.5 text-[10px]">
        <div 
          onclick="openToolRoadmap('${tool.id}')" 
          class="flex items-center space-x-1.5 text-gray-300 hover:text-teal-400 cursor-pointer">
          <span>📄</span>
          <span>${topicsCount} Step-by-Step Topics (Basics → Adv)</span>
        </div>
        <div class="flex items-center justify-between text-gray-300">
          <div class="flex items-center space-x-1.5">
            <span>⚙</span>
            <span>Hands-On Mode</span>
          </div>
          <label class="switch scale-75 origin-right">
            <input type="checkbox" ${tool.hands_on ? 'checked' : ''} onchange="toggleHandsOn('${tool.name}', this.checked)">
            <span class="slider"></span>
          </label>
        </div>
      </div>
    `;

    container.appendChild(card);
  });
}

// Render Knowledge Graph preview widget
function renderKnowledgeGraphPreview(graph) {
  const svg = document.getElementById('interactive-network-svg');
  if (!svg || !graph) return;
  svg.innerHTML = '';

  const defs = document.createElementNS('http://www.w3.org/2000/svg', 'defs');
  defs.innerHTML = `
    <filter id="glow-filter-preview" x="-20%" y="-20%" width="140%" height="140%">
      <feGaussianBlur stdDeviation="3" result="blur"/>
      <feComposite in="SourceGraphic" in2="blur" operator="over"/>
    </filter>
  `;
  svg.appendChild(defs);

  const nodeMap = {};
  graph.nodes.forEach(n => nodeMap[n.id] = n);

  // Links
  graph.links.forEach(l => {
    const s = nodeMap[l.source];
    const t = nodeMap[l.target];
    if (s && t) {
      const line = document.createElementNS('http://www.w3.org/2000/svg', 'line');
      line.setAttribute('x1', s.x);
      line.setAttribute('y1', s.y);
      line.setAttribute('x2', t.x);
      line.setAttribute('y2', t.y);
      line.setAttribute('stroke', '#334155');
      line.setAttribute('stroke-width', '1.5');
      line.setAttribute('stroke-dasharray', '3 2');
      svg.appendChild(line);
    }
  });

  const colorMap = {
    cyan: '#06b6d4',
    orange: '#f97316',
    blue: '#3b82f6',
    purple: '#8b5cf6',
    indigo: '#6366f1'
  };

  // Nodes
  graph.nodes.forEach(n => {
    const g = document.createElementNS('http://www.w3.org/2000/svg', 'g');
    g.style.cursor = 'pointer';
    g.onclick = () => openToolRoadmap(n.id);

    const fill = colorMap[n.group] || '#14b8a6';

    const glow = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
    glow.setAttribute('cx', n.x);
    glow.setAttribute('cy', n.y);
    glow.setAttribute('r', '13');
    glow.setAttribute('fill', fill);
    glow.setAttribute('opacity', '0.25');
    glow.setAttribute('filter', 'url(#glow-filter-preview)');
    g.appendChild(glow);

    const core = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
    core.setAttribute('cx', n.x);
    core.setAttribute('cy', n.y);
    core.setAttribute('r', '8');
    core.setAttribute('fill', fill);
    core.setAttribute('stroke', '#ffffff');
    core.setAttribute('stroke-width', '1.5');
    g.appendChild(core);

    const txt = document.createElementNS('http://www.w3.org/2000/svg', 'text');
    txt.setAttribute('x', n.x);
    txt.setAttribute('y', n.y + 18);
    txt.setAttribute('text-anchor', 'middle');
    txt.setAttribute('fill', '#f1f5f9');
    txt.setAttribute('font-size', '8.5px');
    txt.setAttribute('font-weight', '600');
    txt.textContent = n.label;
    g.appendChild(txt);

    svg.appendChild(g);
  });
}

// Render Challenges cards
function renderChallenges(challenges) {
  const container = document.getElementById('challenges-grid');
  if (!container || !challenges) return;
  container.innerHTML = '';

  challenges.forEach(ch => {
    const item = document.createElement('div');
    item.className = 'bg-[#121a28] hover:bg-[#162235] border border-white/[0.06] rounded-xl p-2.5 flex items-center justify-between cursor-pointer transition';
    item.onclick = () => {
      increaseStreak();
      switchNavTab('study-labs');
    };

    item.innerHTML = `
      <div class="flex-1 pr-2">
        <div class="text-[11px] font-semibold text-white leading-tight mb-1.5">${ch.title}</div>
        <div class="w-full bg-slate-800 h-1.5 rounded-full overflow-hidden">
          <div class="bg-gradient-to-r from-teal-500 to-cyan-400 h-full" style="width: ${ch.progress}%"></div>
        </div>
      </div>
      <div class="w-8 h-8 rounded-lg bg-amber-500/10 border border-amber-500/30 flex flex-col items-center justify-center text-amber-400 flex-shrink-0">
        <span class="text-[9px] font-bold">XP</span>
        <span class="text-[8px] font-semibold leading-none">${ch.xp}</span>
      </div>
    `;
    container.appendChild(item);
  });
}

function increaseStreak() {
  const streakVal = document.getElementById('streak-val');
  const streakBar = document.getElementById('streak-bar');
  if (!streakVal || !streakBar) return;
  let current = parseInt(streakVal.textContent) || 10;
  if (current < 100) {
    current += 5;
    streakVal.textContent = `${current}%`;
    streakBar.style.width = `${current}%`;
  }
}

// ==========================================================================
// 2. Navigation Tabs Switcher
// ==========================================================================

function switchNavTab(tabName) {
  const views = ['dashboard', 'study-labs', 'knowledge-web', 'team-projects', 'community-hub'];
  views.forEach(v => {
    const viewEl = document.getElementById(`view-${v}`);
    const btnEl = document.getElementById(`nav-btn-${v}`);
    if (viewEl) {
      if (v === tabName) {
        viewEl.classList.remove('hidden');
      } else {
        viewEl.classList.add('hidden');
      }
    }
    if (btnEl) {
      if (v === tabName) {
        btnEl.className = 'tab-active text-white px-3.5 py-1.5 rounded-full flex items-center space-x-1.5 shadow-md shadow-teal-500/20 font-semibold cursor-pointer';
      } else {
        btnEl.className = 'text-gray-400 hover:text-white hover:bg-white/[0.06] px-3.5 py-1.5 rounded-full transition flex items-center space-x-1.5 cursor-pointer';
      }
    }
  });

  if (tabName === 'study-labs') {
    initStudyLabs();
  } else if (tabName === 'knowledge-web') {
    initFullKnowledgeWeb();
  }
}

// ==========================================================================
// 3. Tool Step-by-Step Topics Drawer (1, 2, 3, 4... Basics to Advanced)
// ==========================================================================

async function openToolRoadmap(toolId) {
  const modal = document.getElementById('tool-roadmap-modal');
  const titleEl = document.getElementById('roadmap-tool-title');
  const descEl = document.getElementById('roadmap-tool-desc');
  const iconEl = document.getElementById('roadmap-tool-icon');
  const listEl = document.getElementById('roadmap-topics-list');
  const practiceBtn = document.getElementById('roadmap-practice-btn');

  modal.classList.remove('hidden');
  document.body.style.overflow = 'hidden';

  listEl.innerHTML = `<div class="p-8 text-center text-gray-400 text-xs animate-pulse">Loading step-by-step topics...</div>`;

  try {
    const topics = await fetchJson(`/api/lms/tools/${toolId}/topics`);
    
    // Find tool info
    let toolName = toolId.replace('-', ' ').toUpperCase();
    if (dashboardData && dashboardData.toolkit) {
      const match = dashboardData.toolkit.find(t => t.id === toolId);
      if (match) toolName = match.name;
    }

    titleEl.textContent = `${toolName} Topic Curriculum`;
    descEl.textContent = `Topics numbered 1 through ${topics.length} from Basics to Advanced`;
    
    if (toolId.includes('gravity')) iconEl.textContent = '▲';
    else if (toolId.includes('postman')) iconEl.textContent = '🚀';
    else if (toolId.includes('obsidian')) iconEl.textContent = '💎';
    else if (toolId.includes('pomeli')) iconEl.textContent = 'P';
    else if (toolId.includes('claud')) iconEl.textContent = '☁';
    else iconEl.textContent = '🐬';

    listEl.innerHTML = '';

    topics.forEach((top) => {
      const row = document.createElement('div');
      row.className = 'bg-[#121c2d] hover:bg-[#162338] border border-white/[0.06] rounded-xl p-3 flex items-center justify-between transition';

      let levelBadgeClass = 'badge-basics';
      if (top.level === 'Intermediate') levelBadgeClass = 'badge-intermediate';
      if (top.level === 'Advanced') levelBadgeClass = 'badge-advanced';

      row.innerHTML = `
        <div class="flex items-start space-x-3 max-w-[75%]">
          <div class="w-7 h-7 rounded-lg bg-slate-800 text-teal-400 font-bold text-xs flex items-center justify-center flex-shrink-0 border border-teal-500/30">
            ${top.step_number}
          </div>
          <div>
            <div class="flex items-center space-x-2">
              <span class="text-xs font-bold text-white">${top.title}</span>
              <span class="text-[9px] px-2 py-0.5 rounded-full font-semibold ${levelBadgeClass}">${top.level}</span>
            </div>
            <p class="text-[11px] text-gray-400 mt-0.5 leading-snug">${top.summary || 'Fundamental principles and application.'}</p>
          </div>
        </div>
        <button 
          onclick="startTopicLesson('${toolId}', ${top.step_number}, '${encodeURIComponent(top.title)}', '${encodeURIComponent(top.note_path || '')}')" 
          class="bg-teal-600 hover:bg-teal-500 text-white font-bold text-xs px-3 py-1.5 rounded-lg transition shadow-sm flex items-center space-x-1 cursor-pointer">
          <span>Start Topic</span>
          <span>&gt;</span>
        </button>
      `;

      listEl.appendChild(row);
    });

    if (practiceBtn) {
      practiceBtn.onclick = () => {
        modal.classList.add('hidden');
        document.body.style.overflow = '';
        activeStudyTool = toolId;
        switchNavTab('study-labs');
      };
    }

  } catch (e) {
    console.error('Failed to load tool topics:', e);
    listEl.innerHTML = `<div class="p-4 text-rose-400 text-xs">Error loading topics: ${e.message}</div>`;
  }
}

function startTopicLesson(toolId, stepNum, encodedTitle, encodedNotePath) {
  document.getElementById('tool-roadmap-modal').classList.add('hidden');
  document.body.style.overflow = '';

  const title = decodeURIComponent(encodedTitle);
  const notePath = decodeURIComponent(encodedNotePath);

  if (notePath && notePath !== '') {
    openNoteByPath(encodeURIComponent(notePath), `${stepNum}. ${title}`);
  } else {
    // Generate structured lesson on the fly or open relevant note
    openNoteByQuery(toolId);
  }
}

// ==========================================================================
// 4. Study Labs (20 Question Bank & Hands-on Tasks)
// ==========================================================================

async function initStudyLabs() {
  renderStudyToolFilters();
  loadStudyQuestions(activeStudyTool);
  loadStudyTasks(activeStudyTool);
}

function renderStudyToolFilters() {
  const container = document.getElementById('study-labs-tool-filters');
  if (!container) return;
  container.innerHTML = '';

  const tools = [
    { id: 'anti-gravity', name: 'Anti-Gravity' },
    { id: 'postman', name: 'Postman' },
    { id: 'obsidian', name: 'Obsidian' },
    { id: 'pomeli-ai', name: 'Pomeli AI' },
    { id: 'claud-design', name: 'Claud Design' },
    { id: 'sqlite', name: 'SQLite' }
  ];

  tools.forEach(t => {
    const btn = document.createElement('button');
    const isActive = t.id === activeStudyTool;
    btn.className = isActive 
      ? 'bg-teal-600 text-white text-xs px-3 py-1 rounded-full font-bold shadow'
      : 'bg-[#152033] hover:bg-[#1d2c47] text-gray-300 text-xs px-3 py-1 rounded-full font-medium transition';
    btn.textContent = t.name;
    btn.onclick = () => {
      activeStudyTool = t.id;
      renderStudyToolFilters();
      loadStudyQuestions(activeStudyTool);
      loadStudyTasks(activeStudyTool);
    };
    container.appendChild(btn);
  });
}

function switchStudyMode(mode) {
  currentStudyMode = mode;
  const mcqsSec = document.getElementById('study-mcqs-section');
  const tasksSec = document.getElementById('study-tasks-section');
  const mcqBtn = document.getElementById('mode-btn-mcqs');
  const taskBtn = document.getElementById('mode-btn-tasks');

  if (mode === 'mcqs') {
    mcqsSec.classList.remove('hidden');
    tasksSec.classList.add('hidden');
    mcqBtn.className = 'bg-teal-600 text-white px-4 py-1.5 rounded-lg shadow transition font-semibold cursor-pointer';
    taskBtn.className = 'text-gray-400 hover:text-white px-4 py-1.5 rounded-lg transition font-semibold cursor-pointer';
  } else {
    mcqsSec.classList.add('hidden');
    tasksSec.classList.remove('hidden');
    taskBtn.className = 'bg-indigo-600 text-white px-4 py-1.5 rounded-lg shadow transition font-semibold cursor-pointer';
    mcqBtn.className = 'text-gray-400 hover:text-white px-4 py-1.5 rounded-lg transition font-semibold cursor-pointer';
  }
}

async function loadStudyQuestions(toolId) {
  const listEl = document.getElementById('study-mcqs-list');
  const titleEl = document.getElementById('mcq-bank-title');
  const scoreEl = document.getElementById('mcq-score-display');
  if (!listEl) return;

  listEl.innerHTML = `<div class="p-8 text-center text-gray-400 text-xs animate-pulse">Loading Question Bank...</div>`;
  quizScore = 0;
  answeredQuestions.clear();
  if (scoreEl) scoreEl.textContent = '0 / 0';

  try {
    const questions = await fetchJson(`/api/lms/study/questions?tool_id=${toolId}`);
    activeQuestions = questions;

    const toolName = toolId.replace('-', ' ').toUpperCase();
    if (titleEl) titleEl.textContent = `${toolName} Practice Question Bank (${questions.length} Questions)`;

    listEl.innerHTML = '';
    questions.forEach((q, idx) => {
      const qCard = document.createElement('div');
      qCard.className = 'bg-[#111928] border border-slate-700/80 rounded-xl p-4 space-y-3';

      let optionsHtml = '';
      q.options.forEach((opt, oIdx) => {
        optionsHtml += `
          <label class="flex items-center space-x-3 p-2.5 rounded-lg border border-white/[0.06] hover:bg-slate-800/60 cursor-pointer transition text-xs">
            <input type="radio" name="study_q_${idx}" value="${oIdx}" class="text-teal-500 focus:ring-teal-500">
            <span>${opt}</span>
          </label>
        `;
      });

      qCard.innerHTML = `
        <div class="flex items-center justify-between">
          <div class="text-xs font-bold text-white">Q${idx + 1}. ${q.question}</div>
          <span class="text-[10px] text-gray-400">${q.topic_title || 'Core Principle'}</span>
        </div>
        <div class="space-y-1.5">${optionsHtml}</div>
        <div id="study-q-result-${idx}" class="text-xs hidden p-2.5 rounded-lg"></div>
      `;

      // Answer listener
      const inputs = qCard.querySelectorAll(`input[name="study_q_${idx}"]`);
      inputs.forEach(input => {
        input.addEventListener('change', () => {
          const resultDiv = document.getElementById(`study-q-result-${idx}`);
          const selected = parseInt(input.value);
          resultDiv.classList.remove('hidden');

          if (!answeredQuestions.has(idx)) {
            answeredQuestions.add(idx);
            if (selected === q.correct_index) {
              quizScore += 1;
              increaseStreak();
            }
          }

          if (scoreEl) scoreEl.textContent = `${quizScore} / ${answeredQuestions.size}`;

          if (selected === q.correct_index) {
            resultDiv.className = 'text-xs p-2.5 rounded-lg bg-teal-950/60 border border-teal-500/40 text-teal-300 font-semibold';
            resultDiv.innerHTML = `✓ Correct! ${q.explanation}`;
          } else {
            resultDiv.className = 'text-xs p-2.5 rounded-lg bg-rose-950/60 border border-rose-500/40 text-rose-300';
            resultDiv.innerHTML = `✗ Incorrect. Correct answer: Option ${q.correct_index + 1}. ${q.explanation}`;
          }
        });
      });

      listEl.appendChild(qCard);
    });

  } catch (e) {
    console.error('Failed to load study questions:', e);
    listEl.innerHTML = `<div class="p-4 text-rose-400 text-xs">Failed to load questions: ${e.message}</div>`;
  }
}

function resetCurrentQuiz() {
  loadStudyQuestions(activeStudyTool);
}

async function loadStudyTasks(toolId) {
  const listEl = document.getElementById('study-tasks-list');
  if (!listEl) return;

  try {
    const tasks = await fetchJson(`/api/lms/study/tasks?tool_id=${toolId}`);
    listEl.innerHTML = '';

    if (tasks.length === 0) {
      listEl.innerHTML = `<div class="col-span-2 p-6 text-center text-gray-400 text-xs">All practical tasks for ${toolId} completed.</div>`;
      return;
    }

    tasks.forEach(t => {
      const card = document.createElement('div');
      card.className = 'bg-[#111928] border border-white/10 rounded-2xl p-4 flex flex-col justify-between space-y-3';

      let stepsHtml = '';
      t.steps.forEach((s, i) => {
        stepsHtml += `<li class="text-[11px] text-gray-300">${s}</li>`;
      });

      card.innerHTML = `
        <div>
          <div class="flex items-center justify-between mb-2">
            <span class="text-[10px] px-2 py-0.5 rounded-full font-bold bg-indigo-950 text-indigo-400 border border-indigo-500/40 uppercase">${t.difficulty}</span>
            <span class="text-amber-400 text-xs font-bold">⭐ ${t.xp} XP</span>
          </div>
          <h4 class="text-xs font-bold text-white mb-1">${t.title}</h4>
          <p class="text-[11px] text-gray-400 mb-3">${t.description}</p>
          <ul class="list-decimal pl-4 space-y-1 mb-2">${stepsHtml}</ul>
        </div>
        <button 
          onclick="openSandboxModal('${t.title}')" 
          class="w-full bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-xs py-2 rounded-xl transition shadow cursor-pointer">
          Launch in Sandbox Environment &gt;
        </button>
      `;

      listEl.appendChild(card);
    });
  } catch (e) {
    console.error('Failed to load study tasks:', e);
  }
}

// ==========================================================================
// 5. The Knowledge Web (Full View & Link Explanations)
// ==========================================================================

async function initFullKnowledgeWeb() {
  const svg = document.getElementById('full-knowledge-web-svg');
  const list = document.getElementById('knowledge-links-details-list');
  if (!svg || !list) return;

  svg.innerHTML = '';
  list.innerHTML = `<div class="text-gray-400 text-xs animate-pulse">Mapping interconnections...</div>`;

  try {
    const webData = await fetchJson('/api/lms/knowledge-web');
    const links = webData.links;

    // Full Nodes
    const fullNodes = [
      { id: 'anti-gravity', label: 'Anti-Gravity', sub: 'Prioritization', fill: '#06b6d4', x: 80, y: 70 },
      { id: 'postman', label: 'Postman', sub: 'API Testing', fill: '#f97316', x: 200, y: 55 },
      { id: 'obsidian', label: 'Obsidian', sub: 'Knowledge Vault', fill: '#8b5cf6', x: 75, y: 200 },
      { id: 'pomeli-ai', label: 'Pomeli AI', sub: 'Product Research', fill: '#c084fc', x: 170, y: 130 },
      { id: 'claud-design', label: 'Claud Design', sub: 'System Arch.', fill: '#3b82f6', x: 300, y: 110 },
      { id: 'sqlite', label: 'SQLite', sub: 'Data Analytics', fill: '#38bdf8', x: 220, y: 220 },
      { id: 'pm-frameworks', label: 'PM Frameworks', sub: 'Core Models', fill: '#6366f1', x: 350, y: 200 }
    ];

    const nodeMap = {};
    fullNodes.forEach(n => nodeMap[n.id] = n);

    // Render Lines
    links.forEach(l => {
      const s = nodeMap[l.source_id];
      const t = nodeMap[l.target_id];
      if (s && t) {
        const line = document.createElementNS('http://www.w3.org/2000/svg', 'line');
        line.setAttribute('x1', s.x);
        line.setAttribute('y1', s.y);
        line.setAttribute('x2', t.x);
        line.setAttribute('y2', t.y);
        line.setAttribute('stroke', l.strength === 'High' ? '#14b8a6' : '#64748b');
        line.setAttribute('stroke-width', l.strength === 'High' ? '2.5' : '1.5');
        line.setAttribute('opacity', '0.7');
        svg.appendChild(line);
      }
    });

    // Render Nodes
    fullNodes.forEach(n => {
      const g = document.createElementNS('http://www.w3.org/2000/svg', 'g');
      g.style.cursor = 'pointer';
      g.onclick = () => openToolRoadmap(n.id);

      const circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
      circle.setAttribute('cx', n.x);
      circle.setAttribute('cy', n.y);
      circle.setAttribute('r', '12');
      circle.setAttribute('fill', n.fill);
      circle.setAttribute('stroke', '#ffffff');
      circle.setAttribute('stroke-width', '2');
      g.appendChild(circle);

      const txt = document.createElementNS('http://www.w3.org/2000/svg', 'text');
      txt.setAttribute('x', n.x);
      txt.setAttribute('y', n.y + 22);
      txt.setAttribute('text-anchor', 'middle');
      txt.setAttribute('fill', '#f1f5f9');
      txt.setAttribute('font-size', '10px');
      txt.setAttribute('font-weight', '700');
      txt.textContent = n.label;
      g.appendChild(txt);

      const sub = document.createElementNS('http://www.w3.org/2000/svg', 'text');
      sub.setAttribute('x', n.x);
      sub.setAttribute('y', n.y + 32);
      sub.setAttribute('text-anchor', 'middle');
      sub.setAttribute('fill', '#94a3b8');
      sub.setAttribute('font-size', '8px');
      sub.textContent = n.sub;
      g.appendChild(sub);

      svg.appendChild(g);
    });

    // Populate Detailed Link Rationale List
    list.innerHTML = '';
    links.forEach(l => {
      const card = document.createElement('div');
      card.className = 'bg-[#121c2d] border border-white/[0.06] rounded-xl p-3 space-y-1.5 transition hover:border-teal-500/40';

      const sName = (nodeMap[l.source_id] || {}).label || l.source_id;
      const tName = (nodeMap[l.target_id] || {}).label || l.target_id;

      card.innerHTML = `
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-1.5 font-bold text-xs text-white">
            <span class="text-teal-400">${sName}</span>
            <span class="text-gray-400">⟷</span>
            <span class="text-cyan-400">${tName}</span>
          </div>
          <span class="text-[9px] px-2 py-0.5 rounded-full font-bold ${l.strength === 'High' ? 'bg-teal-950 text-teal-300 border border-teal-500/40' : 'bg-slate-800 text-gray-300 border border-slate-700'}">
            Strength: ${l.strength} (${l.weight}%)
          </span>
        </div>
        <p class="text-[11px] text-gray-300 leading-snug"><strong>Why Linked:</strong> ${l.why_linked}</p>
        <p class="text-[10px] text-gray-400 font-mono">Data Flow: ${l.data_flow}</p>
      `;

      list.appendChild(card);
    });

  } catch (e) {
    console.error('Failed to load Knowledge Web:', e);
  }
}

// ==========================================================================
// 6. Settings & Profile Controller (Khushi, Light/Dark Mode, Custom Tools)
// ==========================================================================

async function openSettingsModal(tab = 'profile') {
  const modal = document.getElementById('settings-modal');
  modal.classList.remove('hidden');
  document.body.style.overflow = 'hidden';

  switchSettingsTab(tab);

  try {
    const s = await fetchJson('/api/lms/settings');
    const nameInput = document.getElementById('input-settings-name');
    const avatarInput = document.getElementById('input-settings-avatar');
    const studiedEl = document.getElementById('stats-total-studied');
    const mcqsEl = document.getElementById('stats-mcqs');

    if (nameInput) nameInput.value = s.user_name || 'Khushi';
    if (avatarInput) avatarInput.value = s.avatar_url || '';
    if (studiedEl) studiedEl.textContent = `${s.total_studied || 18} Topics`;
    if (mcqsEl) mcqsEl.textContent = `${s.mcqs_attempted || 42} Attempted`;

    // Render activity logs (Times of day)
    const logsContainer = document.getElementById('activity-logs-container');
    if (logsContainer && s.activity_logs) {
      logsContainer.innerHTML = '';
      s.activity_logs.forEach(log => {
        const item = document.createElement('div');
        item.className = 'p-2.5 rounded-lg bg-slate-800/40 border border-white/[0.04] flex items-center justify-between text-xs';
        item.innerHTML = `
          <div>
            <div class="font-bold text-white">${log.action}</div>
            <div class="text-[10px] text-gray-400">${log.details || ''}</div>
          </div>
          <span class="text-teal-400 text-[10px] font-mono bg-teal-950/60 px-2 py-0.5 rounded border border-teal-500/20">${log.time_of_day}</span>
        `;
        logsContainer.appendChild(item);
      });
    }

    renderManageToolsList();

  } catch (e) {
    console.error('Failed to load settings:', e);
  }
}

function switchSettingsTab(tabName) {
  const tabs = ['profile', 'appearance', 'history', 'notifications', 'tools', 'classes'];
  tabs.forEach(t => {
    const panel = document.getElementById(`set-panel-${t}`);
    const btn = document.getElementById(`tab-btn-set-${t}`);
    if (panel) {
      if (t === tabName) panel.classList.remove('hidden');
      else panel.classList.add('hidden');
    }
    if (btn) {
      if (t === tabName) {
        btn.className = 'tab-active text-white px-3 py-2 rounded-t-lg font-semibold';
      } else {
        btn.className = 'text-gray-400 hover:text-white px-3 py-2 rounded-t-lg font-semibold';
      }
    }
  });
}

async function saveUserProfile() {
  const nameInput = document.getElementById('input-settings-name');
  const avatarInput = document.getElementById('input-settings-avatar');
  const newName = nameInput.value.trim() || 'Khushi';
  const newAvatar = avatarInput.value.trim();

  try {
    await fetchJson('/api/lms/settings', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ user_name: newName, avatar_url: newAvatar })
    });

    const headerName = document.getElementById('header-user-name');
    const headerAvatar = document.getElementById('header-avatar');
    if (headerName) headerName.textContent = newName;
    if (headerAvatar && newAvatar) headerAvatar.src = newAvatar;

    alert(`Profile saved successfully! Welcome back, ${newName}.`);
  } catch (e) {
    alert(`Failed to save profile: ${e.message}`);
  }
}

function setTheme(theme, persist = true) {
  currentTheme = theme;
  const body = document.body;
  const label = document.getElementById('current-theme-label');
  const darkBtn = document.getElementById('theme-btn-dark');
  const lightBtn = document.getElementById('theme-btn-light');

  if (theme === 'light') {
    body.classList.add('light-mode');
    document.documentElement.classList.remove('dark');
    if (label) label.textContent = 'Active: Clean Ivory Light Mode';
    if (lightBtn) lightBtn.className = 'bg-teal-600 text-white px-3 py-1.5 rounded-lg font-bold shadow';
    if (darkBtn) darkBtn.className = 'bg-slate-300 text-gray-800 px-3 py-1.5 rounded-lg font-bold';
  } else {
    body.classList.remove('light-mode');
    document.documentElement.classList.add('dark');
    if (label) label.textContent = 'Active: Dark Cyberpunk Mode';
    if (darkBtn) darkBtn.className = 'bg-teal-600 text-white px-3 py-1.5 rounded-lg font-bold shadow';
    if (lightBtn) lightBtn.className = 'bg-slate-700 text-gray-200 px-3 py-1.5 rounded-lg font-bold';
  }

  if (persist) {
    fetchJson('/api/lms/settings', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ theme })
    }).catch(e => console.error(e));
  }
}

// Add New Tool by User
async function addNewTool() {
  const nameInput = document.getElementById('new-tool-name');
  const catInput = document.getElementById('new-tool-category');
  const topicsInput = document.getElementById('new-tool-topics');

  const name = nameInput.value.trim();
  const category = catInput.value.trim();
  const rawTopics = topicsInput.value.trim();

  if (!name || !category) {
    alert('Please enter both tool name and category.');
    return;
  }

  const topics = rawTopics ? rawTopics.split(',').map(s => s.trim()).filter(Boolean) : [];

  try {
    await fetchJson('/api/lms/tools', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ id: name.toLowerCase().replace(/\s+/g, '-'), name, category, topics })
    });

    nameInput.value = '';
    catInput.value = '';
    topicsInput.value = '';

    alert(`Tool "${name}" added successfully!`);
    initDashboard();
    renderManageToolsList();
  } catch (e) {
    alert(`Failed to add tool: ${e.message}`);
  }
}

async function renderManageToolsList() {
  const list = document.getElementById('manage-tools-list');
  if (!list) return;

  try {
    const data = await fetchJson('/api/lms/dashboard');
    list.innerHTML = '';
    data.toolkit.forEach(t => {
      const row = document.createElement('div');
      row.className = 'p-2 rounded-lg bg-slate-800/40 border border-white/[0.05] flex items-center justify-between text-xs';
      row.innerHTML = `
        <div class="flex items-center space-x-2">
          <span class="font-bold text-white">${t.name}</span>
          <span class="text-gray-400">(${t.category})</span>
        </div>
        <button onclick="eliminateTool('${t.id}', '${t.name}')" class="text-rose-400 hover:text-rose-300 font-bold px-2 py-0.5 rounded border border-rose-500/30 hover:bg-rose-950/40 cursor-pointer">
          Eliminate ✖
        </button>
      `;
      list.appendChild(row);
    });
  } catch (e) {
    console.error(e);
  }
}

async function eliminateTool(toolId, toolName) {
  if (!confirm(`Are you sure you want to eliminate "${toolName}" from your toolkit?`)) return;

  try {
    await fetchJson(`/api/lms/tools/${toolId}`, { method: 'DELETE' });
    alert(`Tool "${toolName}" eliminated.`);
    initDashboard();
    renderManageToolsList();
  } catch (e) {
    alert(`Failed to eliminate tool: ${e.message}`);
  }
}

// Add New Daily Class by User
async function addNewDailyClass() {
  const tagInput = document.getElementById('new-class-tag');
  const numInput = document.getElementById('new-class-num');
  const titleInput = document.getElementById('new-class-title');
  const subInput = document.getElementById('new-class-sub');

  const tag = tagInput.value.trim();
  const num = parseInt(numInput.value) || 8;
  const title = titleInput.value.trim();
  const sub = subInput.value.trim();

  if (!tag || !title) {
    alert('Please enter day tag and class title.');
    return;
  }

  try {
    await fetchJson('/api/lms/daily-classes', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ day_number: num, day_tag: tag, title, subtitle: sub })
    });

    tagInput.value = '';
    titleInput.value = '';
    subInput.value = '';

    alert(`Daily Class "${title}" added to curriculum!`);
    initDashboard();
  } catch (e) {
    alert(`Failed to add daily class: ${e.message}`);
  }
}

// ==========================================================================
// 7. Note Reader Modal with Mermaid & Markdown Rendering
// ==========================================================================

async function openNoteByPath(encodedPath, title) {
  const modal = document.getElementById('note-reader-modal');
  const readerTitle = document.getElementById('reader-title');
  const readerPath = document.getElementById('reader-path');
  const markdownContainer = document.getElementById('reader-markdown-container');
  const footerSource = document.getElementById('reader-footer-source');

  const decodedPath = decodeURIComponent(encodedPath);
  currentNotePath = decodedPath;
  currentTopicTitle = title || decodedPath.split(/[/\\]/).pop().replace('.md', '');

  readerTitle.textContent = currentTopicTitle;
  readerPath.textContent = decodedPath;
  footerSource.textContent = decodedPath;
  markdownContainer.innerHTML = `
    <div class="animate-pulse space-y-4">
      <div class="h-6 bg-slate-800 rounded w-1/3"></div>
      <div class="h-4 bg-slate-800 rounded w-full"></div>
      <div class="h-4 bg-slate-800 rounded w-5/6"></div>
    </div>`;

  modal.classList.remove('hidden');
  document.body.style.overflow = 'hidden';

  switchReaderTab('content');

  try {
    const data = await fetchJson(`/api/notes/content?path=${encodedPath}`);
    markdownContainer.innerHTML = data.html;

    const mermaidCodes = markdownContainer.querySelectorAll('code.language-mermaid');
    mermaidCodes.forEach((codeBlock) => {
      const pre = codeBlock.parentElement;
      const mermaidDiv = document.createElement('div');
      mermaidDiv.className = 'mermaid';
      mermaidDiv.textContent = codeBlock.textContent;
      pre.parentNode.replaceChild(mermaidDiv, pre);
    });

    if (window.mermaid) {
      setTimeout(() => {
        mermaid.run({
          nodes: markdownContainer.querySelectorAll('.mermaid')
        });
      }, 50);
    }

    generateModalQuizQuestions(currentTopicTitle);

  } catch (err) {
    console.error('Failed to load note content:', err);
    markdownContainer.innerHTML = `<div class="p-4 bg-rose-950/40 border border-rose-500/50 rounded-xl text-rose-300 text-sm">Note loaded or simulated: ${err.message}</div>`;
  }
}

async function openNoteByQuery(query) {
  try {
    const notes = await fetchJson(`/api/notes/search?q=${encodeURIComponent(query)}`);
    if (notes && notes.length > 0) {
      openNoteByPath(encodeURIComponent(notes[0].note_path), notes[0].note_title);
    } else {
      openVaultModal();
    }
  } catch (e) {
    console.error('Search failed:', e);
    openVaultModal();
  }
}

function generateModalQuizQuestions(topicTitle) {
  const container = document.getElementById('quiz-questions-list');
  if (!container) return;

  const mockQuestions = [
    {
      q: `What is the core architectural principle emphasized in ${topicTitle}?`,
      options: [
        'Decoupling modules to scale independently and prevent cascading failures',
        'Statically compiling all dependencies into a single monolithic binary',
        'Disabling caching to preserve total data consistency at all costs',
        'Directly exposing internal database credentials to client devices'
      ],
      correct: 0,
      explanation: 'Decoupling services and managing boundaries prevents single-point failure and allows horizontal scalability.'
    },
    {
      q: 'Which HTTP status code designates an unauthenticated request?',
      options: [
        '400 Bad Request',
        '401 Unauthorized',
        '403 Forbidden',
        '404 Not Found'
      ],
      correct: 1,
      explanation: '401 Unauthorized signifies missing or invalid authentication credentials.'
    }
  ];

  container.innerHTML = '';
  mockQuestions.forEach((item, idx) => {
    const qCard = document.createElement('div');
    qCard.className = 'bg-[#111928] border border-slate-700/80 rounded-xl p-4 space-y-3';

    let optionsHtml = '';
    item.options.forEach((opt, oIdx) => {
      optionsHtml += `
        <label class="flex items-center space-x-3 p-2.5 rounded-lg border border-white/[0.06] hover:bg-slate-800/60 cursor-pointer transition text-xs">
          <input type="radio" name="modal_q_${idx}" value="${oIdx}" class="text-teal-500 focus:ring-teal-500">
          <span>${opt}</span>
        </label>
      `;
    });

    qCard.innerHTML = `
      <div class="text-xs font-bold text-white">Q${idx + 1}. ${item.q}</div>
      <div class="space-y-1.5">${optionsHtml}</div>
      <div id="modal-quiz-result-${idx}" class="text-xs hidden p-2.5 rounded-lg"></div>
    `;

    const inputs = qCard.querySelectorAll(`input[name="modal_q_${idx}"]`);
    inputs.forEach(input => {
      input.addEventListener('change', () => {
        const resultDiv = document.getElementById(`modal-quiz-result-${idx}`);
        const selected = parseInt(input.value);
        resultDiv.classList.remove('hidden');
        if (selected === item.correct) {
          resultDiv.className = 'text-xs p-2.5 rounded-lg bg-teal-950/60 border border-teal-500/40 text-teal-300 font-semibold';
          resultDiv.innerHTML = `✓ Correct! ${item.explanation}`;
          increaseStreak();
        } else {
          resultDiv.className = 'text-xs p-2.5 rounded-lg bg-rose-950/60 border border-rose-500/40 text-rose-300';
          resultDiv.innerHTML = `✗ Incorrect. ${item.explanation}`;
        }
      });
    });

    container.appendChild(qCard);
  });
}

function switchReaderTab(tab) {
  const contentTab = document.getElementById('tab-note-content');
  const quizTab = document.getElementById('tab-note-quiz');
  const markdownContainer = document.getElementById('reader-markdown-container');
  const quizContainer = document.getElementById('reader-quiz-container');

  if (tab === 'content') {
    contentTab.className = 'bg-teal-600 text-white text-xs px-3 py-1.5 rounded-lg font-semibold transition cursor-pointer';
    quizTab.className = 'bg-[#192336] hover:bg-[#22314a] text-gray-300 text-xs px-3 py-1.5 rounded-lg font-semibold transition cursor-pointer';
    markdownContainer.classList.remove('hidden');
    quizContainer.classList.add('hidden');
  } else {
    quizTab.className = 'bg-teal-600 text-white text-xs px-3 py-1.5 rounded-lg font-semibold transition cursor-pointer';
    contentTab.className = 'bg-[#192336] hover:bg-[#22314a] text-gray-300 text-xs px-3 py-1.5 rounded-lg font-semibold transition cursor-pointer';
    markdownContainer.classList.add('hidden');
    quizContainer.classList.remove('hidden');
  }
}

// ==========================================================================
// 8. Obsidian Vault Modal & Catalog
// ==========================================================================

async function openVaultModal() {
  const modal = document.getElementById('vault-modal');
  modal.classList.remove('hidden');
  document.body.style.overflow = 'hidden';

  try {
    const statusData = await fetchJson('/api/vault/status');
    const totalCount = document.getElementById('vault-total-count');
    if (totalCount) totalCount.textContent = `${statusData.total_notes} Notes`;

    const topics = await fetchJson('/api/topics');
    const list = document.getElementById('vault-notes-list');
    list.innerHTML = '';

    for (const topic of topics) {
      const notes = await fetchJson(`/api/topics/${topic.id}/notes`);
      if (notes.length === 0) continue;

      const groupHeader = document.createElement('div');
      groupHeader.className = 'text-[11px] font-bold text-teal-400 mt-2 mb-1 uppercase tracking-wider flex items-center justify-between';
      groupHeader.innerHTML = `<span>📁 ${topic.title}</span><span class="text-gray-500 font-normal">(${notes.length})</span>`;
      list.appendChild(groupHeader);

      notes.forEach(n => {
        const row = document.createElement('div');
        row.className = 'bg-[#121c2c] hover:bg-slate-800 border border-white/[0.04] p-2 rounded-lg flex items-center justify-between text-xs cursor-pointer transition';
        row.onclick = () => {
          modal.classList.add('hidden');
          document.body.style.overflow = '';
          openNoteByPath(encodeURIComponent(n.note_path), n.note_title);
        };
        row.innerHTML = `
          <div class="flex items-center space-x-2 truncate">
            <span>📄</span>
            <span class="text-gray-200 hover:text-white font-medium">${n.note_title}</span>
          </div>
          <span class="text-[10px] text-teal-400 hover:underline flex-shrink-0">Read &gt;</span>
        `;
        list.appendChild(row);
      });
    }

    const filterInput = document.getElementById('vault-filter-input');
    if (filterInput) {
      filterInput.oninput = (e) => {
        const val = e.target.value.toLowerCase();
        list.querySelectorAll('div.bg-\\[\\#121c2c\\]').forEach(el => {
          el.style.display = el.textContent.toLowerCase().includes(val) ? 'flex' : 'none';
        });
      };
    }

  } catch (e) {
    console.error('Failed to load vault status:', e);
  }
}

// ==========================================================================
// 9. Interactive Terminal / Sandbox
// ==========================================================================

function openSandboxModal(title) {
  const modal = document.getElementById('sandbox-modal');
  const titleEl = document.getElementById('sandbox-title');
  if (titleEl) titleEl.textContent = `${title} [Interactive Sandbox]`;
  modal.classList.remove('hidden');
  document.body.style.overflow = 'hidden';

  const input = document.getElementById('terminal-input');
  if (input) input.focus();
}

function setupTerminal() {
  const input = document.getElementById('terminal-input');
  const btn = document.getElementById('terminal-exec-btn');
  const output = document.getElementById('terminal-output');

  function runCmd() {
    if (!input || !output) return;
    const cmd = input.value.trim();
    if (!cmd) return;

    const pUser = document.createElement('p');
    pUser.innerHTML = `<span class="text-teal-400">user@earlylearn</span>:<span class="text-cyan-400">~/sandbox</span>$ ${cmd}`;
    output.appendChild(pUser);

    const pResp = document.createElement('p');
    pResp.className = 'text-gray-300';

    if (cmd === 'help') {
      pResp.innerHTML = `Available commands:<br>• <strong>notes</strong>: List recent vault notes<br>• <strong>sql</strong>: Run mock query SELECT * FROM events<br>• <strong>test-api</strong>: Simulate Day 4 Postman test<br>• <strong>clear</strong>: Clear screen`;
    } else if (cmd === 'notes') {
      pResp.innerHTML = `Loaded 79 notes from D:\\o.vault<br>• Session-04-APIs-Integrations.md<br>• ER-Diagrams.md<br>• SQL-Basics.md<br>• Cloud-Computing.md`;
    } else if (cmd === 'sql') {
      pResp.innerHTML = `Executing: SELECT title, capacity FROM events LIMIT 3;<br>+--------------------------+----------+<br>| AI Hackathon             | 100      |<br>| Robotics Workshop        | 50       |<br>| Battle of Bands          | 300      |<br>+--------------------------+----------+`;
      increaseStreak();
    } else if (cmd === 'test-api') {
      pResp.innerHTML = `<span class="text-teal-400">POST /api/v1/authenticate HTTP/1.1</span><br>Status: 200 OK<br>Token: eyJhbGciOiJIUzI1Ni... (JWT Valid)<br><span class="text-emerald-400">✓ All 5 API Tests Passed! (+50 XP)</span>`;
      increaseStreak();
    } else if (cmd === 'clear') {
      output.innerHTML = '';
      input.value = '';
      return;
    } else {
      pResp.innerHTML = `Command executed: <em>${cmd}</em> (exit code 0)`;
    }

    output.appendChild(pResp);
    output.scrollTop = output.scrollHeight;
    input.value = '';
  }

  if (btn) btn.onclick = runCmd;
  if (input) {
    input.onkeydown = (e) => {
      if (e.key === 'Enter') runCmd();
    };
  }
}

// ==========================================================================
// 10. Global Search & Autocomplete
// ==========================================================================

function setupSearch() {
  const searchInput = document.getElementById('global-search-input');
  const dropdown = document.getElementById('search-dropdown');
  const resultsList = document.getElementById('search-results-list');
  let debounceTimeout = null;

  if (!searchInput || !dropdown || !resultsList) return;

  searchInput.addEventListener('input', () => {
    clearTimeout(debounceTimeout);
    const query = searchInput.value.trim();

    if (query.length < 1) {
      dropdown.classList.add('hidden');
      return;
    }

    debounceTimeout = setTimeout(async () => {
      try {
        const results = await fetchJson(`/api/notes/search?q=${encodeURIComponent(query)}`);
        resultsList.innerHTML = '';

        if (results.length === 0) {
          resultsList.innerHTML = '<div class="text-xs text-gray-400 p-2">No matching notes or frameworks found.</div>';
        } else {
          results.forEach(item => {
            const row = document.createElement('div');
            row.className = 'p-2 hover:bg-slate-800 rounded-lg cursor-pointer flex items-center justify-between text-xs transition';
            row.onclick = () => {
              dropdown.classList.add('hidden');
              searchInput.value = '';
              openNoteByPath(encodeURIComponent(item.note_path), item.note_title);
            };
            row.innerHTML = `
              <div>
                <div class="font-semibold text-white">${item.note_title}</div>
                <div class="text-[10px] text-gray-400">${item.topic_title}</div>
              </div>
              <span class="text-teal-400 text-[10px]">Open ➔</span>
            `;
            resultsList.appendChild(row);
          });
        }

        dropdown.classList.remove('hidden');
      } catch (e) {
        console.error('Search error:', e);
      }
    }, 200);
  });

  document.addEventListener('click', (e) => {
    if (!searchInput.contains(e.target) && !dropdown.contains(e.target)) {
      dropdown.classList.add('hidden');
    }
  });
}

// ==========================================================================
// 11. Modal Setup & Event Listeners
// ==========================================================================

function setupModals() {
  const closeRoadmap = document.getElementById('close-roadmap-modal');
  if (closeRoadmap) {
    closeRoadmap.onclick = () => {
      document.getElementById('tool-roadmap-modal').classList.add('hidden');
      document.body.style.overflow = '';
    };
  }

  const closeSettings = document.getElementById('close-settings-modal');
  if (closeSettings) {
    closeSettings.onclick = () => {
      document.getElementById('settings-modal').classList.add('hidden');
      document.body.style.overflow = '';
    };
  }

  const closeReader = document.getElementById('close-reader-modal');
  if (closeReader) {
    closeReader.onclick = () => {
      document.getElementById('note-reader-modal').classList.add('hidden');
      document.body.style.overflow = '';
    };
  }

  const closeVault = document.getElementById('close-vault-modal');
  if (closeVault) {
    closeVault.onclick = () => {
      document.getElementById('vault-modal').classList.add('hidden');
      document.body.style.overflow = '';
    };
  }

  const closeSandbox = document.getElementById('close-sandbox-modal');
  if (closeSandbox) {
    closeSandbox.onclick = () => {
      document.getElementById('sandbox-modal').classList.add('hidden');
      document.body.style.overflow = '';
    };
  }

  const tabContent = document.getElementById('tab-note-content');
  const tabQuiz = document.getElementById('tab-note-quiz');
  if (tabContent) tabContent.onclick = () => switchReaderTab('content');
  if (tabQuiz) tabQuiz.onclick = () => switchReaderTab('quiz');

  const markBtn = document.getElementById('mark-understood-btn');
  if (markBtn) {
    markBtn.onclick = () => {
      markBtn.textContent = '✓ Understood (+50 XP Claimed!)';
      markBtn.className = 'bg-emerald-600 text-white font-bold px-3 py-1.5 rounded-lg';
      increaseStreak();
    };
  }

  const toggleDaily = document.getElementById('toggle-daily');
  const toggleWeekly = document.getElementById('toggle-weekly');
  if (toggleDaily && toggleWeekly) {
    toggleDaily.onclick = () => {
      toggleDaily.className = 'bg-teal-600 text-white px-2.5 py-0.5 rounded-full transition cursor-pointer';
      toggleWeekly.className = 'text-gray-400 hover:text-white px-2.5 py-0.5 rounded-full transition cursor-pointer';
    };
    toggleWeekly.onclick = () => {
      toggleWeekly.className = 'bg-teal-600 text-white px-2.5 py-0.5 rounded-full transition cursor-pointer';
      toggleDaily.className = 'text-gray-400 hover:text-white px-2.5 py-0.5 rounded-full transition cursor-pointer';
    };
  }
}

// Global window actions
window.switchNavTab = switchNavTab;
window.openToolRoadmap = openToolRoadmap;
window.startTopicLesson = startTopicLesson;
window.switchStudyMode = switchStudyMode;
window.resetCurrentQuiz = resetCurrentQuiz;
window.openSettingsModal = openSettingsModal;
window.switchSettingsTab = switchSettingsTab;
window.saveUserProfile = saveUserProfile;
window.setTheme = setTheme;
window.addNewTool = addNewTool;
window.eliminateTool = eliminateTool;
window.addNewDailyClass = addNewDailyClass;
window.openNoteByPath = openNoteByPath;
window.openNoteByQuery = openNoteByQuery;
window.openVaultModal = openVaultModal;
window.openSandboxModal = openSandboxModal;
window.openLiveLab = () => openNoteByQuery('Session-04-APIs-Integrations');
window.downloadSandboxSpec = () => alert('Sandbox environment specification downloaded: Postman-API-Testing-Spec.json');
window.toggleHandsOn = (toolName, isChecked) => console.log(`Hands-on mode for ${toolName}: ${isChecked}`);

// Initialize on DOM ready
document.addEventListener('DOMContentLoaded', () => {
  initDashboard();
  setupSearch();
  setupModals();
  setupTerminal();
});

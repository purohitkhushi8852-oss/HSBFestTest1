/**
 * Spider-Man in Diwali Attire with Interactive Cursor Web Slinger
 * For Zenith '26 College Fest Management System
 * 
 * Features:
 * - Spider-Man wearing festive Indian Diwali attire:
 *   Saffron/maroon embroidered Kurta, golden Zari border, flowing festive stole (dupatta), and golden kadas.
 * - Dynamic jumping acrobatics between screen anchor points with squash & stretch physics.
 * - Continuous high-precision tensile web shooting from Spidey's wrist directly to the user's cursor.
 * - Festive Diwali sparkler particle bursts at the web's cursor contact point.
 * - Friendly comic speech bubbles on click/jump.
 * - Zero blocking: pointer-events: none ensures full usability of forms & cards.
 */

(function () {
  // Prevent duplicate initialization
  if (window.__SPIDERMAN_DIWALI_LOADED__) return;
  window.__SPIDERMAN_DIWALI_LOADED__ = true;

  // Configuration
  const CONFIG = {
    jumpIntervalMin: 3500,
    jumpIntervalMax: 6500,
    jumpDuration: 900,
    spideySize: 130, // px
    webSagFactor: 0.12,
    sparkleCount: 18,
    greetings: [
      "Happy Diwali! May your code be bug-free! 🪔✨",
      "Web locked onto your cursor! Wherever you go, I follow! 🕸️",
      "With great power comes great Diwali sweets! 💥",
      "Zenith '26 is officially lit! ⚡🎉",
      "Careful with that cursor, you're swinging with Spider-Man! 🕷️",
      "Festive vibes only! Rocking the Diwali Sherwani! 🥻✨"
    ]
  };

  // State
  let isEnabled = true;
  let mouseX = window.innerWidth / 2;
  let mouseY = window.innerHeight / 2;
  let targetX = 120;
  let targetY = 160;
  let currentX = targetX;
  let currentY = targetY;
  let startX = targetX;
  let startY = targetY;
  let jumpProgress = 1; // 1 = landed
  let isJumping = false;
  let jumpStartTime = 0;
  let facingRight = true;
  let nextJumpTimer = null;
  let speechTimeout = null;
  // Shooting state variables
  let isShooting = false;
  let shootTargetX = 0;
  let shootTargetY = 0;
  let shootStartX = 0;
  let shootStartY = 0;
  let shootStartTime = 0;
  const shootDuration = 300; // ms


  // Activate Spider on mousedown and start drawing web
  // Activate Spider shooting on click and start drawing thin web
  window.addEventListener('mousedown', (e) => {
    // Begin shooting towards click position
    isShooting = true;
    shootStartX = currentX;
    shootStartY = currentY;
    // Target position (center Spider on click)
    shootTargetX = e.clientX - CONFIG.spideySize / 2;
    shootTargetY = e.clientY - CONFIG.spideySize;
    shootStartTime = performance.now();
    // Update mouse position for web drawing
    mouseX = e.clientX;
    mouseY = e.clientY;
  });

  window.addEventListener('mouseup', (e) => {
    // Clear canvas on mouse up; spider remains visible
    ctx.clearRect(0, 0, canvas.width, canvas.height);
  });

  // Create Canvas Layer for Web & Sparks
  const canvas = document.createElement("canvas");
  canvas.id = "spidey-web-canvas";
  canvas.style.position = "fixed";
  canvas.style.top = "0";
  canvas.style.left = "0";
  canvas.style.width = "100vw";
  canvas.style.height = "100vh";
  canvas.style.pointerEvents = "none";
  canvas.style.zIndex = "9998";
  document.body.appendChild(canvas);
  const ctx = canvas.getContext("2d");

  function resizeCanvas() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
  }
  window.addEventListener("resize", resizeCanvas);
  resizeCanvas();

  // Create Spider-Man DOM Container
  const spideyContainer = document.createElement("div");
  spideyContainer.id = "diwali-spiderman-actor";
  spideyContainer.style.position = "fixed";
  spideyContainer.style.width = `${CONFIG.spideySize}px`;
  spideyContainer.style.height = `${CONFIG.spideySize}px`;
  spideyContainer.style.left = "0px";
  spideyContainer.style.top = "0px";
  spideyContainer.style.transformOrigin = "center bottom";
  spideyContainer.style.zIndex = "9999";
  spideyContainer.style.cursor = "pointer";
  spideyContainer.style.userSelect = "none";
  spideyContainer.style.transition = "filter 0.3s ease";
  spideyContainer.setAttribute("title", "Click to make Diwali Spidey jump!");

  // Speech bubble element
  const speechBubble = document.createElement("div");
  speechBubble.id = "spidey-speech-bubble";
  speechBubble.style.position = "absolute";
  speechBubble.style.bottom = "115%";
  speechBubble.style.left = "50%";
  speechBubble.style.transform = "translateX(-50%) scale(0)";
  speechBubble.style.transformOrigin = "bottom center";
  speechBubble.style.backgroundColor = "#fff";
  speechBubble.style.color = "#0f172a";
  speechBubble.style.fontWeight = "bold";
  speechBubble.style.fontSize = "11px";
  speechBubble.style.padding = "6px 12px";
  speechBubble.style.borderRadius = "12px";
  speechBubble.style.boxShadow = "0 8px 24px rgba(0,0,0,0.3), 0 0 0 2px #d97706";
  speechBubble.style.whiteSpace = "nowrap";
  speechBubble.style.pointerEvents = "none";
  speechBubble.style.transition = "transform 0.25s cubic-bezier(0.175, 0.885, 0.32, 1.275), opacity 0.2s ease";
  speechBubble.style.opacity = "0";
  speechBubble.innerHTML = "Happy Diwali! 🪔";
  spideyContainer.appendChild(speechBubble);

  // High-Resolution Vector SVG of Spider-Man in Diwali Attire
  // Costume: Spider-Man classic mask + deep crimson & saffron festive kurta + golden zari borders + flowing stole (dupatta) + golden kada
  const spideySvgWrapper = document.createElement("div");
  spideySvgWrapper.id = "spidey-svg-body";
  spideySvgWrapper.style.width = "100%";
  spideySvgWrapper.style.height = "100%";
  spideySvgWrapper.innerHTML = `
    <svg viewBox="0 0 160 160" width="100%" height="100%" style="overflow:visible; filter: drop-shadow(0 10px 14px rgba(0,0,0,0.45));">
      <defs>
        <!-- Gold Brocade Gradient -->
        <linearGradient id="goldZari" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#fef08a" />
          <stop offset="50%" stop-color="#eab308" />
          <stop offset="100%" stop-color="#b45309" />
        </linearGradient>

        <!-- Saffron Festive Silk -->
        <linearGradient id="saffronSilk" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#fb923c" />
          <stop offset="60%" stop-color="#ea580c" />
          <stop offset="100%" stop-color="#9a3412" />
        </linearGradient>

        <!-- Spidey Crimson Suit -->
        <radialGradient id="spideyRed" cx="40%" cy="40%" r="65%">
          <stop offset="0%" stop-color="#ef4444" />
          <stop offset="70%" stop-color="#b91c1c" />
          <stop offset="100%" stop-color="#7f1d1d" />
        </radialGradient>

        <!-- Spidey Navy Accent -->
        <linearGradient id="spideyBlue" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" stop-color="#1e3a8a" />
          <stop offset="100%" stop-color="#0f172a" />
        </linearGradient>

        <!-- Diya Glow Filter -->
        <filter id="festiveGlow" x="-20%" y="-20%" width="140%" height="140%">
          <feGaussianBlur stdDeviation="3" result="blur" />
          <feComposite in="SourceGraphic" in2="blur" operator="over" />
        </filter>
      </defs>

      <!-- Flowing Festive Stole (Dupatta) Fluttering in the Wind -->
      <path id="dupatta-trail" d="M 52,70 C 25,60 10,80 -5,68 C -15,58 -8,45 8,50 C 25,55 45,62 56,66 Z" 
            fill="url(#goldZari)" opacity="0.95" stroke="#ca8a04" stroke-width="1.2">
        <animate attributeName="d" 
                 values="M 52,70 C 25,60 10,80 -5,68 C -15,58 -8,45 8,50 C 25,55 45,62 56,66 Z;
                         M 52,70 C 20,72 5,55 -10,75 C -20,85 -5,95 12,80 C 28,68 45,64 56,66 Z;
                         M 52,70 C 25,60 10,80 -5,68 C -15,58 -8,45 8,50 C 25,55 45,62 56,66 Z"
                 dur="2.4s" repeatCount="indefinite" />
      </path>

      <!-- Spidey Legs in crouch/spring pose -->
      <!-- Left Leg -->
      <path d="M 60,110 Q 35,115 32,135 Q 40,145 55,140 Q 62,128 66,114 Z" fill="url(#spideyBlue)" stroke="#090d16" stroke-width="1.5"/>
      <!-- Right Leg (bent high) -->
      <path d="M 95,110 Q 120,112 125,130 Q 118,142 102,138 Q 94,125 90,112 Z" fill="url(#spideyBlue)" stroke="#090d16" stroke-width="1.5"/>
      <!-- Golden Jutti / Festive Footwear Accents -->
      <path d="M 32,135 Q 35,145 50,142 Q 44,136 34,134 Z" fill="url(#goldZari)"/>
      <path d="M 125,130 Q 122,142 107,139 Q 114,133 124,129 Z" fill="url(#goldZari)"/>

      <!-- Torso: Diwali Embroidered Kurta Over Spider Suit -->
      <path d="M 54,65 Q 80,60 102,65 Q 106,98 96,118 Q 80,122 58,118 Q 50,95 54,65 Z" 
            fill="url(#saffronSilk)" stroke="#9a3412" stroke-width="1.5" />

      <!-- Kurta Center Placket & Zari Embroidery -->
      <line x1="78" y1="64" x2="78" y2="118" stroke="url(#goldZari)" stroke-width="3" stroke-linecap="round" />
      <!-- Festive Buttons / Golden Diyas on Placket -->
      <circle cx="78" cy="74" r="2" fill="#fef08a" />
      <circle cx="78" cy="86" r="2" fill="#fef08a" />
      <circle cx="78" cy="98" r="2" fill="#fef08a" />
      <circle cx="78" cy="110" r="2" fill="#fef08a" />

      <!-- Zari Border on Kurta Hem -->
      <path d="M 58,115 Q 78,121 96,115" stroke="url(#goldZari)" stroke-width="3" fill="none" />

      <!-- Left Arm (Posed back in balance) -->
      <path d="M 55,68 Q 30,75 22,95 Q 26,102 34,98 Q 42,85 58,74 Z" fill="url(#spideyRed)" stroke="#7f1d1d" stroke-width="1.2"/>
      <!-- Left Golden Kada / Wristband -->
      <rect x="23" y="93" width="9" height="5" rx="2" fill="url(#goldZari)" transform="rotate(-25 27 95)"/>

      <!-- Right Arm (Web Shooter Forward!) -->
      <!-- Kurta sleeve on shoulder -->
      <path d="M 100,68 Q 120,72 136,88 Q 140,82 132,74 Q 116,62 100,66 Z" fill="url(#saffronSilk)" stroke="#9a3412" stroke-width="1.2"/>
      <!-- Forearm & Hand in Classic Spidey Web-Shooting Pose (Middle fingers down, index & pinky out) -->
      <path d="M 132,84 Q 142,94 148,92 Q 152,90 148,84 Q 140,78 132,82 Z" fill="url(#spideyRed)" stroke="#7f1d1d" stroke-width="1.2"/>
      <!-- Golden Kada / Web-Shooter on Right Wrist (Emitter Origin: ~148, 90) -->
      <ellipse cx="144" cy="88" rx="4" ry="7" fill="url(#goldZari)" stroke="#ca8a04" stroke-width="1"/>
      <circle id="spidey-emitter-node" cx="147" cy="89" r="3" fill="#38bdf8" filter="url(#festiveGlow)"/>
      <!-- Fuljhadi (firecracker) held in hand -->
      <g id="fuljhadi">
        <rect x="145" y="85" width="4" height="6" fill="url(#goldZari)" stroke="#ca8a04" stroke-width="0.5"/>
        <circle cx="147" cy="84" r="1.5" fill="#facc15"/>
      </g>

      <!-- Festive Dupatta / Stole Over Chest -->
      <path d="M 64,64 Q 78,82 92,106 Q 84,112 70,88 Q 62,76 64,64 Z" fill="url(#goldZari)" opacity="0.9"/>

      <!-- Spidey Mask / Head -->
      <ellipse cx="78" cy="42" rx="26" ry="30" fill="url(#spideyRed)" stroke="#7f1d1d" stroke-width="1.5"/>

      <!-- Mask Web Lines -->
      <path d="M 78,12 L 78,72 M 52,42 L 104,42" stroke="#7f1d1d" stroke-width="0.8" opacity="0.7"/>
      <path d="M 60,22 Q 78,30 96,22" stroke="#7f1d1d" stroke-width="0.8" fill="none" opacity="0.7"/>
      <path d="M 54,34 Q 78,44 102,34" stroke="#7f1d1d" stroke-width="0.8" fill="none" opacity="0.7"/>
      <path d="M 56,52 Q 78,42 100,52" stroke="#7f1d1d" stroke-width="0.8" fill="none" opacity="0.7"/>
      <path d="M 62,62 Q 78,54 94,62" stroke="#7f1d1d" stroke-width="0.8" fill="none" opacity="0.7"/>

      <!-- Diwali Tilak on Spidey Mask Forehead (Auspicious Festive Touch) -->
      <path d="M 78,18 Q 80,24 78,30 Q 76,24 78,18 Z" fill="#eab308" filter="url(#festiveGlow)"/>
      <circle cx="78" cy="32" r="1.5" fill="#f43f5e" />

      <!-- Expressive Spider Eyes (Big, Expressive, Classic Comic Look) -->
      <!-- Left Eye -->
      <path d="M 57,40 Q 64,30 73,38 Q 67,48 57,40 Z" fill="#000" />
      <path d="M 59,39 Q 64,32 71,38 Q 66,46 59,39 Z" fill="#ffffff" />
      <!-- Right Eye -->
      <path d="M 99,40 Q 92,30 83,38 Q 89,48 99,40 Z" fill="#000" />
      <path d="M 97,39 Q 92,32 85,38 Q 90,46 97,39 Z" fill="#ffffff" />

      <!-- Mini Diwali Sparkler / Diya suspended above Spidey -->
      <g transform="translate(118, 18)" filter="url(#festiveGlow)">
        <path d="M 0,10 Q 8,10 12,14 Q -2,16 -2,12 Z" fill="#d97706" />
        <path d="M 4,10 Q 7,2 5,0 Q 3,2 4,10 Z" fill="#facc15" />
        <circle cx="5" cy="0" r="1.5" fill="#ffffff" />
      </g>
    </svg>
  `;
  spideyContainer.appendChild(spideySvgWrapper);
  document.body.appendChild(spideyContainer);

  // Floating Control Pill Widget (Bottom Right)
  const controlPill = document.createElement("div");
  controlPill.id = "diwali-spidey-controls";
  controlPill.style.position = "fixed";
  controlPill.style.bottom = "20px";
  controlPill.style.right = "20px";
  controlPill.style.zIndex = "10000";
  controlPill.style.display = "flex";
  controlPill.style.alignItems = "center";
  controlPill.style.gap = "8px";
  controlPill.style.padding = "6px 12px";
  controlPill.style.borderRadius = "9999px";
  controlPill.style.backgroundColor = "rgba(15, 23, 42, 0.9)";
  controlPill.style.backdropFilter = "blur(8px)";
  controlPill.style.border = "1px solid rgba(234, 179, 8, 0.4)";
  controlPill.style.boxShadow = "0 8px 24px rgba(0, 0, 0, 0.4), 0 0 16px rgba(234, 179, 8, 0.2)";
  controlPill.style.fontFamily = "system-ui, -apple-system, sans-serif";
  controlPill.style.fontSize = "12px";
  controlPill.style.color = "#fef08a";
  controlPill.style.userSelect = "none";

  controlPill.innerHTML = `
    <span style="font-size: 14px;">🪔</span>
    <span style="font-weight: 700; color: #fff;">Diwali Spidey</span>
    <button id="spidey-toggle-btn" style="background:#ea580c; color:#fff; border:none; padding:3px 9px; border-radius:9999px; font-weight:700; font-size:11px; cursor:pointer; transition:all 0.2s;">ON</button>
    <button id="spidey-jump-btn" style="background:rgba(255,255,255,0.1); color:#38bdf8; border:1px solid rgba(56,189,248,0.4); padding:3px 9px; border-radius:9999px; font-weight:700; font-size:11px; cursor:pointer;" title="Trigger high leap!">Leap! ⚡</button>
  `;
  document.body.appendChild(controlPill);

  // Sound effect via Web Audio API (gentle web thwip & festive chime)
  function playWebSound() {
    try {
      const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
      const osc = audioCtx.createOscillator();
      const gain = audioCtx.createGain();
      osc.type = "sine";
      osc.frequency.setValueAtTime(440, audioCtx.currentTime);
      osc.frequency.exponentialRampToValueAtTime(120, audioCtx.currentTime + 0.12);
      gain.gain.setValueAtTime(0.08, audioCtx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.001, audioCtx.currentTime + 0.12);
      osc.connect(gain);
      gain.connect(audioCtx.destination);
      osc.start();
      osc.stop(audioCtx.currentTime + 0.13);
    } catch (e) {
      // Audio not permitted or not supported
    }
  }

  // Anchor Positions across the screen
  function getAnchorPositions() {
    const pad = 80;
    const w = window.innerWidth;
    const h = window.innerHeight;
    return [
      { x: pad, y: pad + 40, anchorName: "Top-Left Banner" },
      { x: w - pad - CONFIG.spideySize, y: pad + 40, anchorName: "Top-Right Rafter" },
      { x: w - pad - CONFIG.spideySize - 60, y: h * 0.45, anchorName: "Mid-Right Wall" },
      { x: pad + 40, y: h * 0.55, anchorName: "Mid-Left Wall" },
      { x: w * 0.5 - CONFIG.spideySize / 2, y: pad + 20, anchorName: "Fest Ceiling Arch" },
      { x: pad + 120, y: h - pad - CONFIG.spideySize, anchorName: "Bottom-Left Stage" },
      { x: w - pad - CONFIG.spideySize - 140, y: h - pad - CONFIG.spideySize - 40, anchorName: "Bottom-Right Podium" },
    ];
  }

  // Trigger Spidey Leap
  function triggerJump(customTarget) {
    if (isJumping || !isEnabled) return;

    isJumping = true;
    jumpStartTime = performance.now();
    startX = currentX;
    startY = currentY;

    if (customTarget) {
      targetX = customTarget.x;
      targetY = customTarget.y;
    } else {
      const anchors = getAnchorPositions();
      // Pick anchor different from current
      let bestAnchor = anchors[Math.floor(Math.random() * anchors.length)];
      for (let i = 0; i < 5; i++) {
        const candidate = anchors[Math.floor(Math.random() * anchors.length)];
        const dist = Math.hypot(candidate.x - currentX, candidate.y - currentY);
        if (dist > 250) {
          bestAnchor = candidate;
          break;
        }
      }
      targetX = bestAnchor.x;
      targetY = bestAnchor.y;
    }

    facingRight = targetX >= startX;
    playWebSound();

    // Spawn jump sparkle explosion at takeoff
    spawnBurst(startX + CONFIG.spideySize / 2, startY + CONFIG.spideySize / 2, 22);

    // Speak a random festive greeting occasionally
    if (Math.random() < 0.45) {
      speakGreeting();
    }
  }

  function scheduleNextJump() {
    clearTimeout(nextJumpTimer);
    if (!isEnabled) return;
    const delay = CONFIG.jumpIntervalMin + Math.random() * (CONFIG.jumpIntervalMax - CONFIG.jumpIntervalMin);
    nextJumpTimer = setTimeout(() => {
      triggerJump();
    }, delay);
  }

  // Speech Bubble Handler
  function speakGreeting(customText) {
    clearTimeout(speechTimeout);
    const text = customText || CONFIG.greetings[Math.floor(Math.random() * CONFIG.greetings.length)];
    speechBubble.innerHTML = text;
    speechBubble.style.transform = "translateX(-50%) scale(1)";
    speechBubble.style.opacity = "1";

    speechTimeout = setTimeout(() => {
      speechBubble.style.transform = "translateX(-50%) scale(0)";
      speechBubble.style.opacity = "0";
    }, 3800);
  }

  // Mouse / Touch Tracking
  function onPointerMove(e) {
    if (e.clientX !== undefined) {
      mouseX = e.clientX;
      mouseY = e.clientY;
    } else if (e.touches && e.touches.length > 0) {
      mouseX = e.touches[0].clientX;
      mouseY = e.touches[0].clientY;
    }
  }
  window.addEventListener("mousemove", onPointerMove, { passive: true });
  window.addEventListener("touchmove", onPointerMove, { passive: true });

  // Burst Particles on Cursor Web Contact
  function spawnBurst(x, y, count = 12) {
    const festiveColors = ["#facc15", "#f59e0b", "#ec4899", "#38bdf8", "#ffffff", "#ea580c"];
    for (let i = 0; i < count; i++) {
      const angle = Math.random() * Math.PI * 2;
      const speed = 1.5 + Math.random() * 4.5;
      particles.push({
        x: x,
        y: y,
        vx: Math.cos(angle) * speed,
        vy: Math.sin(angle) * speed - 0.5,
        radius: 1.5 + Math.random() * 2.5,
        color: festiveColors[Math.floor(Math.random() * festiveColors.length)],
        life: 1.0,
        decay: 0.02 + Math.random() * 0.035,
        sparkle: Math.random() > 0.4
      });
    }
  }

  // Get Spidey's Active Wrist Position in Viewport
  function getWristCoordinates() {
    // Emitter node is near top-right/top-left of Spidey SVG depending on facing
    const offsetX = facingRight ? CONFIG.spideySize * 0.90 : CONFIG.spideySize * 0.10;
    const offsetY = CONFIG.spideySize * 0.56;
    return {
      x: currentX + offsetX,
      y: currentY + offsetY
    };
  }

  // Animation Frame Loop
  function animate(now) {
    requestAnimationFrame(animate);

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Show spider only when enabled (always present)
    if (!isEnabled) {
      spideyContainer.style.display = "none";
      // Clear canvas when hidden
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      return;
    } else {
      spideyContainer.style.display = "block";
    }

    // 1. Update Spidey Jump Physics
    if (isJumping) {
      const elapsed = now - jumpStartTime;
      const t = Math.min(1, elapsed / CONFIG.jumpDuration);

      // Parabolic jump arc with easeInOut
      // Horizontal linear interpolation
      currentX = startX + (targetX - startX) * t;

      // Vertical arc: height peak in the middle
      const arcHeight = Math.max(120, Math.abs(targetX - startX) * 0.45);
      const verticalArc = 4 * arcHeight * t * (1 - t);
      const linearY = startY + (targetY - startY) * t;
      currentY = linearY - verticalArc;

      // Squash and stretch during jump
      let scaleX = 1;
      let scaleY = 1;
      let rotation = 0;

      if (t < 0.15) {
        // Pre-jump crouch (squash)
        scaleX = 1.25;
        scaleY = 0.75;
      } else if (t < 0.85) {
        // Mid-air stretch along velocity direction
        scaleX = 0.85;
        scaleY = 1.15;
        const jumpAngle = Math.atan2((targetY - startY) - verticalArc, targetX - startX);
        rotation = jumpAngle * 0.3 * (facingRight ? 1 : -1);
      } else {
        // Landing cushion
        scaleX = 1.2;
        scaleY = 0.8;
      }

      const flipTransform = facingRight ? "scaleX(1)" : "scaleX(-1)";
      spideyContainer.style.transform = `translate3d(${currentX}px, ${currentY}px, 0) ${flipTransform} rotate(${rotation}rad) scale(${scaleX}, ${scaleY})`;

      if (t >= 1) {
        isJumping = false;
        currentX = targetX;
        currentY = targetY;
        spawnBurst(currentX + CONFIG.spideySize / 2, currentY + CONFIG.spideySize * 0.8, 16);
        playWebSound();
        scheduleNextJump();
      }
    } else {
      // Idle breathing and slight bobbing
      const idleBob = Math.sin(now * 0.0035) * 4;
      const flipTransform = facingRight ? "scaleX(1)" : "scaleX(-1)";
      spideyContainer.style.transform = `translate3d(${currentX}px, ${currentY + idleBob}px, 0) ${flipTransform}`;
    }

// 2. Handle shooting movement and draw thin web
if (isShooting) {
  const elapsed = now - shootStartTime;
  const t = Math.min(1, elapsed / shootDuration);
  // Linear interpolation towards target
  currentX = shootStartX + (shootTargetX - shootStartX) * t;
  currentY = shootStartY + (shootTargetY - shootStartY) * t;
  // Draw thin web line from wrist to cursor
  const wrist = getWristCoordinates();
  ctx.save();
  ctx.beginPath();
  ctx.moveTo(wrist.x, wrist.y);
  ctx.lineTo(mouseX, mouseY);
  ctx.strokeStyle = "rgba(180,180,180,0.6)";
  ctx.lineWidth = 1;
  ctx.stroke();
  ctx.restore();

  // Spark from fuljhadi firecracker (occasional)
  if (Math.random() < 0.3) {
    spawnBurst(wrist.x, wrist.y, 2);
  }

  if (t >= 1) {
    isShooting = false;
    // Optional burst at arrival
    spawnBurst(currentX + CONFIG.spideySize / 2, currentY + CONFIG.spideySize * 0.8, 12);
    playWebSound();
  }
}
// 3. Update & Draw Particles (Diwali Sparklers at cursor and Spidey)
updateAndDrawParticles();

// Continuous mini-sparks where the web touches the cursor (only when shooting)
if (isShooting && Math.random() < 0.35) {
  spawnBurst(mouseX, mouseY, 2);
}
  }

  // Draw High-Tech Glowing Tensile Silk Web
  function drawDynamicWeb(wx, wy, cx, cy, now) {
    const dist = Math.hypot(cx - wx, cy - wy);
    if (dist < 5) return;

    // Sag calculation (web sags naturally with gravity)
    const midX = (wx + cx) / 2;
    const midY = (wy + cy) / 2 + Math.min(80, dist * CONFIG.webSagFactor);

    // Subtle vibration wave on web string
    const vibration = Math.sin(now * 0.012) * Math.min(6, dist * 0.015);
    const ctrlX = midX;
    const ctrlY = midY + vibration;

    ctx.save();

    // Outer subtle cyan glow (lighter)
    ctx.beginPath();
    ctx.moveTo(wx, wy);
    ctx.quadraticCurveTo(ctrlX, ctrlY, cx, cy);
    ctx.strokeStyle = "rgba(166, 214, 255, 0.3)"; // pastel cyan
    ctx.lineWidth = 3; // reduced from 6
    ctx.lineCap = "round";
    ctx.stroke();

    // Inner festive gold (softer)
    ctx.beginPath();
    ctx.moveTo(wx, wy);
    ctx.quadraticCurveTo(ctrlX, ctrlY, cx, cy);
    ctx.strokeStyle = "rgba(255, 221, 153, 0.6)"; // softer gold
    ctx.lineWidth = 2; // reduced from 3.2
    ctx.stroke();

    // Pure white silk core line (thin)
    ctx.beginPath();
    ctx.moveTo(wx, wy);
    ctx.quadraticCurveTo(ctrlX, ctrlY, cx, cy);
    ctx.strokeStyle = "#ffffff";
    ctx.lineWidth = 1; // reduced from 1.6
    ctx.stroke();

    // Interlaced web nodes / beads along strand
    const nodeCount = 5;
    for (let i = 1; i < nodeCount; i++) {
      const frac = i / nodeCount;
      // Quadratic bezier interpolation
      const inv = 1 - frac;
      const nx = inv * inv * wx + 2 * inv * frac * ctrlX + frac * frac * cx;
      const ny = inv * inv * wy + 2 * inv * frac * ctrlY + frac * frac * cy;

      ctx.beginPath();
      ctx.arc(nx, ny, 2.2, 0, Math.PI * 2);
      ctx.fillStyle = i % 2 === 0 ? "#facc15" : "#38bdf8";
      ctx.shadowColor = "#facc15";
      ctx.shadowBlur = 6;
      ctx.fill();
    }

    // Web Anchor Cross / Spider Ring at Cursor
    ctx.beginPath();
    ctx.arc(cx, cy, 5, 0, Math.PI * 2);
    ctx.fillStyle = "#ffffff";
    ctx.shadowColor = "#38bdf8";
    ctx.shadowBlur = 10;
    ctx.fill();

    ctx.beginPath();
    ctx.arc(cx, cy, 9, 0, Math.PI * 2);
    ctx.strokeStyle = "rgba(250, 204, 21, 0.85)";
    ctx.lineWidth = 1.5;
    ctx.stroke();

    ctx.restore();
  }

  // Particle System Update
  function updateAndDrawParticles() {
    ctx.save();
    for (let i = particles.length - 1; i >= 0; i--) {
      const p = particles[i];
      p.x += p.vx;
      p.y += p.vy;
      p.vy += 0.08; // gravity
      p.life -= p.decay;

      if (p.life <= 0) {
        particles.splice(i, 1);
        continue;
      }

      ctx.beginPath();
      ctx.arc(p.x, p.y, p.radius * p.life, 0, Math.PI * 2);
      ctx.fillStyle = p.color;
      ctx.globalAlpha = p.life;
      ctx.shadowColor = p.color;
      ctx.shadowBlur = p.sparkle ? 8 : 2;
      ctx.fill();
    }
    ctx.restore();
  }

  // User Interactivity
  spideyContainer.addEventListener("click", () => {
    speakGreeting("Thwip! Catch me if you can! 🕷️✨");
    triggerJump();
  });

  const toggleBtn = controlPill.querySelector("#spidey-toggle-btn");
  toggleBtn.addEventListener("click", () => {
    isEnabled = !isEnabled;
    if (isEnabled) {
      toggleBtn.textContent = "ON";
      toggleBtn.style.background = "#ea580c";
      speakGreeting("Spider-Man is back in action! 🪔");
      scheduleNextJump();
    } else {
      toggleBtn.textContent = "OFF";
      toggleBtn.style.background = "#64748b";
      clearTimeout(nextJumpTimer);
    }
  });

  const leapBtn = controlPill.querySelector("#spidey-jump-btn");
  leapBtn.addEventListener("click", () => {
    if (!isEnabled) {
      isEnabled = true;
      toggleBtn.textContent = "ON";
      toggleBtn.style.background = "#ea580c";
    }
    triggerJump();
  });

  // Start Animation
  requestAnimationFrame(animate);
  scheduleNextJump();

  // Initial welcome greeting after 1.2s
  setTimeout(() => {
    speakGreeting("Happy Diwali! Spider-Man is slinging webs at Zenith '26! 🪔✨");
  }, 1200);

  console.log("🕷️ Diwali Spider-Man Web Animation initialized successfully!");
})();
